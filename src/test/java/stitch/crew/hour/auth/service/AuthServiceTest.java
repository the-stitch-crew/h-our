package stitch.crew.hour.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import stitch.crew.hour.auth.domain.RefreshToken;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.dto.OAuthSignupInfoResponse;
import stitch.crew.hour.auth.dto.OAuthSignupPayload;
import stitch.crew.hour.auth.dto.OAuthSignupRequest;
import stitch.crew.hour.auth.dto.RefreshTokenRequest;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService의")
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private SignupTokenStore oauthSignupTokenStore;

	@Nested
	@DisplayName("Describe: login 메서드는")
	class Describe_login {

		@Test
		@DisplayName("It: 이메일과 비밀번호가 일치하면 토큰 정보를 반환한다")
		void it_returns_tokens_when_credentials_are_valid() {
			// given
			LoginRequest request = createLoginRequest();
			User user = createUser();

			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
			given(jwtTokenProvider.issueKeyPair(user.getEmail(), user.getRole()))
				.willReturn(new KeyPair("access-token", "refresh-token"));

			// when
			KeyPair response = authService.login(request);

			// then
			assertThat(response.accessToken()).isEqualTo("access-token");
			assertThat(response.refreshToken()).isEqualTo("refresh-token");
		}

		@Test
		@DisplayName("It: 이메일이 존재하지 않으면 LOGIN_FAILED 예외가 발생한다")
		void it_throws_invalid_credentials_when_email_does_not_exist() {
			// given
			LoginRequest request = createLoginRequest();
			given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.login(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
			verify(passwordEncoder, never()).matches(request.password(), "encodedPassword");
		}

		@Test
		@DisplayName("It: 비밀번호가 일치하지 않으면 LOGIN_FAILED 예외가 발생한다")
		void it_throws_invalid_credentials_when_password_does_not_match() {
			// given
			LoginRequest request = createLoginRequest();
			User user = createUser();

			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.login(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
			verify(jwtTokenProvider, never()).issueKeyPair(user.getEmail(), user.getRole());
		}

		@Test
		@DisplayName("It: 삭제된 회원이면 ALREADY_DELETED 예외가 발생한다")
		void it_throws_already_deleted_when_user_is_deleted() {
			// given
			LoginRequest request = createLoginRequest();
			User user = createUser();
			user.delete();

			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.login(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_DELETED);
			verify(jwtTokenProvider, never()).issueKeyPair(user.getEmail(), user.getRole());
		}
	}

	@Nested
	@DisplayName("Describe: oauthSignup 메서드는")
	class Describe_oauthSignup {

		@Test
		@DisplayName("It: OAuth 추가정보가 유효하면 OAuth 회원을 생성하고 토큰 정보를 반환한다")
		void it_creates_oauth_user_and_returns_tokens() {
			// given
			OAuthSignupRequest request = createOAuthSignupRequest();
			OAuthSignupPayload signupPayload = createOAuthSignupPayload("GOOGLE");
			KeyPair keyPair = new KeyPair("oauth-access-token", "oauth-refresh-token");

			given(oauthSignupTokenStore.find(request.signupToken()))
				.willReturn(Optional.of(signupPayload));
			given(userRepository.existsByEmail(signupPayload.email())).willReturn(false);
			given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(false);
			given(passwordEncoder.encode(any(String.class))).willReturn("encoded-random-password");
			given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
			given(jwtTokenProvider.issueKeyPair(signupPayload.email(), Role.USER)).willReturn(keyPair);

			// when
			KeyPair response = authService.oauthSignup(request);

			// then
			assertThat(response.accessToken()).isEqualTo("oauth-access-token");
			assertThat(response.refreshToken()).isEqualTo("oauth-refresh-token");

			ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userCaptor.capture());
			User savedUser = userCaptor.getValue();

			assertThat(savedUser.getEmail()).isEqualTo(signupPayload.email());
			assertThat(savedUser.getUserName()).isEqualTo(signupPayload.userName());
			assertThat(savedUser.getProvider()).isEqualTo("GOOGLE");
			assertThat(savedUser.getIsAuthLinked()).isTrue();
			assertThat(savedUser.getPassword()).isEqualTo("encoded-random-password");
			assertThat(savedUser.getRole()).isEqualTo(Role.USER);
			verify(oauthSignupTokenStore).delete(request.signupToken());
		}

		@Test
		@DisplayName("It: 이미 가입된 이메일이면 USER_EMAIL_ALREADY_EXISTS 예외가 발생한다")
		void it_throws_when_email_already_exists() {
			// given
			OAuthSignupRequest request = createOAuthSignupRequest();
			OAuthSignupPayload signupPayload = createOAuthSignupPayload("GOOGLE");
			given(oauthSignupTokenStore.find(request.signupToken()))
				.willReturn(Optional.of(signupPayload));
			given(userRepository.existsByEmail(signupPayload.email())).willReturn(true);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.oauthSignup(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
			verify(userRepository, never()).save(any(User.class));
			verify(jwtTokenProvider, never()).issueKeyPair(signupPayload.email(), Role.USER);
			verify(oauthSignupTokenStore, never()).delete(request.signupToken());
		}

		@Test
		@DisplayName("It: 이미 가입된 전화번호이면 USER_PHONE_ALREADY_EXISTS 예외가 발생한다")
		void it_throws_when_phone_number_already_exists() {
			// given
			OAuthSignupRequest request = createOAuthSignupRequest();
			OAuthSignupPayload signupPayload = createOAuthSignupPayload("GOOGLE");
			given(oauthSignupTokenStore.find(request.signupToken()))
				.willReturn(Optional.of(signupPayload));
			given(userRepository.existsByEmail(signupPayload.email())).willReturn(false);
			given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(true);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.oauthSignup(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_PHONE_ALREADY_EXISTS);
			verify(userRepository, never()).save(any(User.class));
			verify(jwtTokenProvider, never()).issueKeyPair(signupPayload.email(), Role.USER);
			verify(oauthSignupTokenStore, never()).delete(request.signupToken());
		}

		@Test
		@DisplayName("It: Google이 아닌 provider이면 VALIDATION_FAILED 예외가 발생한다")
		void it_throws_when_provider_is_not_google() {
			// given
			OAuthSignupRequest request = new OAuthSignupRequest(
				"signup-token",
				LocalDate.of(2000, 1, 1),
				Gender.MALE,
				"010-9999-8888",
				"KOREA"
			);
			given(oauthSignupTokenStore.find(request.signupToken()))
				.willReturn(Optional.of(createOAuthSignupPayload("KAKAO")));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.oauthSignup(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VALIDATION_FAILED);
			verify(userRepository, never()).existsByEmail("oauth@google.com");
			verify(userRepository, never()).existsByPhoneNumber(request.phoneNumber());
			verify(userRepository, never()).save(any(User.class));
			verify(oauthSignupTokenStore, never()).delete(request.signupToken());
		}

		@Test
		@DisplayName("It: signupToken이 유효하지 않으면 INVALID_SIGNUP_TOKEN 예외가 발생한다")
		void it_throws_when_signup_token_is_invalid() {
			// given
			OAuthSignupRequest request = createOAuthSignupRequest();
			given(oauthSignupTokenStore.find(request.signupToken())).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.oauthSignup(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_SIGNUP_TOKEN);
			verify(userRepository, never()).existsByEmail("oauth@google.com");
			verify(userRepository, never()).save(any(User.class));
			verify(oauthSignupTokenStore, never()).delete(request.signupToken());
		}
	}

	@Nested
	@DisplayName("Describe: getOAuthSignupInfo 메서드는")
	class Describe_getOAuthSignupInfo {

		@Test
		@DisplayName("It: 유효한 signupToken이면 OAuth 회원가입 고정 정보를 반환한다")
		void it_returns_oauth_signup_info_when_signup_token_is_valid() {
			// given
			String signupToken = "signup-token";
			OAuthSignupPayload signupPayload = createOAuthSignupPayload("GOOGLE");

			given(oauthSignupTokenStore.find(signupToken)).willReturn(Optional.of(signupPayload));

			// when
			OAuthSignupInfoResponse response = authService.getOAuthSignupInfo(signupToken);

			// then
			assertThat(response.email()).isEqualTo(signupPayload.email());
			assertThat(response.userName()).isEqualTo(signupPayload.userName());
			assertThat(response.provider()).isEqualTo(signupPayload.provider());
		}

		@Test
		@DisplayName("It: signupToken이 유효하지 않으면 INVALID_SIGNUP_TOKEN 예외가 발생한다")
		void it_throws_when_signup_token_is_invalid() {
			// given
			String signupToken = "invalid-signup-token";
			given(oauthSignupTokenStore.find(signupToken)).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.getOAuthSignupInfo(signupToken)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_SIGNUP_TOKEN);
		}
	}

	@Nested
	@DisplayName("Describe: refresh 메서드는")
	class Describe_refresh {

		@Test
		@DisplayName("It: 유효한 리프레시 토큰이면 기존 토큰을 삭제하고 새 토큰 정보를 반환한다")
		void it_deletes_old_refresh_token_and_returns_new_tokens() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			RefreshToken refreshToken = createRefreshToken(request.refreshToken());
			User user = createUser();

			given(refreshTokenRepository.findByRefreshToken(request.refreshToken()))
				.willReturn(Optional.of(refreshToken));
			given(jwtTokenProvider.parseJwt(request.refreshToken()))
				.willReturn(new TokenBody(user.getEmail()));
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
			given(jwtTokenProvider.issueKeyPair(user.getEmail(), user.getRole()))
				.willReturn(new KeyPair("new-access-token", "new-refresh-token"));

			// when
			KeyPair response = authService.refresh(request);

			// then
			assertThat(response.accessToken()).isEqualTo("new-access-token");
			assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
			verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
			verify(refreshTokenRepository).delete(refreshToken);
		}

		@Test
		@DisplayName("It: 저장된 리프레시 토큰이 아니면 INVALID_REFRESH_TOKEN 예외가 발생한다")
		void it_throws_invalid_refresh_token_when_token_is_not_stored() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			given(refreshTokenRepository.findByRefreshToken(request.refreshToken()))
				.willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.refresh(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
			verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
			verify(jwtTokenProvider, never()).issueKeyPair("legend@naver.com", Role.USER);
		}

		@Test
		@DisplayName("It: 리프레시 토큰 검증에 실패하면 해당 예외가 발생한다")
		void it_throws_when_refresh_token_validation_fails() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("access-token");
			willThrow(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN))
				.given(jwtTokenProvider)
				.validateRefreshToken(request.refreshToken());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.refresh(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
			verify(refreshTokenRepository, never()).findByRefreshToken(request.refreshToken());
		}

		@Test
		@DisplayName("It: 저장된 이메일과 토큰 이메일이 다르면 INVALID_REFRESH_TOKEN 예외가 발생한다")
		void it_throws_invalid_refresh_token_when_email_does_not_match() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			RefreshToken refreshToken = createRefreshToken(request.refreshToken());

			given(refreshTokenRepository.findByRefreshToken(request.refreshToken()))
				.willReturn(Optional.of(refreshToken));
			given(jwtTokenProvider.parseJwt(request.refreshToken()))
				.willReturn(new TokenBody("other@naver.com"));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.refresh(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
			verify(userRepository, never()).findByEmail("other@naver.com");
			verify(jwtTokenProvider, never()).issueKeyPair("other@naver.com", Role.USER);
		}
	}

	@Nested
	@DisplayName("Describe: logout 메서드는")
	class Describe_logout {

		@Test
		@DisplayName("It: 유효한 리프레시 토큰이면 저장된 토큰을 삭제한다")
		void it_deletes_refresh_token() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			RefreshToken refreshToken = createRefreshToken(request.refreshToken());

			given(refreshTokenRepository.findByRefreshToken(request.refreshToken()))
				.willReturn(Optional.of(refreshToken));

			// when
			authService.logout(request);

			// then
			verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
			verify(refreshTokenRepository).delete(refreshToken);
		}

		@Test
		@DisplayName("It: 저장된 리프레시 토큰이 아니면 INVALID_REFRESH_TOKEN 예외가 발생한다")
		void it_throws_invalid_refresh_token_when_token_is_not_stored() {
			// given
			RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
			given(refreshTokenRepository.findByRefreshToken(request.refreshToken()))
				.willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> authService.logout(request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
			verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
		}
	}

	private LoginRequest createLoginRequest() {
		return new LoginRequest(
			"legend@naver.com",
			"password123"
		);
	}

	private OAuthSignupRequest createOAuthSignupRequest() {
		return new OAuthSignupRequest(
			"signup-token",
			LocalDate.of(2000, 1, 1),
			Gender.MALE,
			"010-9999-8888",
			"KOREA"
		);
	}

	private OAuthSignupPayload createOAuthSignupPayload(String provider) {
		return new OAuthSignupPayload(
			"test@google.com",
			"namename",
			provider
		);
	}

	private User createUser() {
		User user = new User(
			"대정수",
			"legend@naver.com",
			"encodedPassword",
			LocalDate.of(2000, 1, 1),
			Role.USER,
			Gender.MALE,
			null,
			"010-1234-5678",
			"KOREA",
			false,
			false
		);
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}

	private RefreshToken createRefreshToken(String token) {
		return RefreshToken.builder()
			.refreshToken(token)
			.email("legend@naver.com")
			.build();
	}
}
