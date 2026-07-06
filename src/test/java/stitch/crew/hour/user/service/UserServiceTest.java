package stitch.crew.hour.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.PasswordChangeRequest;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.UserRoleUpdateRequest;
import stitch.crew.hour.user.dto.UserUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService의")
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Nested
	@DisplayName("Describe: signup 메서드는")
	class Describe_signup {

		@Nested
		@DisplayName("Context: 올바른 회원가입 요청이 주어지면")
		class Context_with_valid_request {

			SignupRequest request;

			@BeforeEach
			void setUp() {
				request = createSignupRequest();
			}

			@Test
			@DisplayName("It: 회원을 저장하고 userId를 반환한다")
			void it_saves_user_and_returns_user_id() {
				// given
				given(userRepository.existsByEmail(request.email())).willReturn(false);
				given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(false);
				given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
				given(userRepository.save(any(User.class))).willAnswer(invocation -> {
					User user = invocation.getArgument(0);
					ReflectionTestUtils.setField(user, "id", 1L);
					return user;
				});

				// when
				SignupResponse response = userService.signup(request);

				// then
				assertThat(response.userId()).isEqualTo(1L);

				ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
				verify(userRepository).save(captor.capture());
				User savedUser = captor.getValue();

				assertThat(savedUser.getUserName()).isEqualTo(request.userName());
				assertThat(savedUser.getEmail()).isEqualTo(request.email());
				assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
				assertThat(savedUser.getBirthDate()).isEqualTo(request.birthDate());
				assertThat(savedUser.getRole()).isEqualTo(Role.USER);
				assertThat(savedUser.getGender()).isEqualTo(request.gender());
				assertThat(savedUser.getPhoneNumber()).isEqualTo(request.phoneNumber());
				assertThat(savedUser.getNationality()).isEqualTo(request.nationality());
				assertThat(savedUser.getIsAuthLinked()).isFalse();
				assertThat(savedUser.getIdBlack()).isFalse();
			}
		}

		@Nested
		@DisplayName("Context: 이미 존재하는 이메일이 주어지면")
		class Context_with_existing_email {

			@Test
			@DisplayName("It: USER_EMAIL_ALREADY_EXISTS 예외가 발생한다")
			void it_throws_email_already_exists() {
				// given
				SignupRequest request = createSignupRequest();
				given(userRepository.existsByEmail(request.email())).willReturn(true);

				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userService.signup(request)
				);

				// then
				assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
				verify(userRepository, never()).existsByPhoneNumber(any());
				verify(passwordEncoder, never()).encode(any());
				verify(userRepository, never()).save(any(User.class));
			}
		}

		@Nested
		@DisplayName("Context: 이미 존재하는 전화번호가 주어지면")
		class Context_with_existing_phone_number {

			@Test
			@DisplayName("It: USER_PHONE_ALREADY_EXISTS 예외가 발생한다")
			void it_throws_phone_already_exists() {
				// given
				SignupRequest request = createSignupRequest();
				given(userRepository.existsByEmail(request.email())).willReturn(false);
				given(userRepository.existsByPhoneNumber(request.phoneNumber())).willReturn(true);

				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> userService.signup(request)
				);

				// then
				assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_PHONE_ALREADY_EXISTS);
				verify(passwordEncoder, never()).encode(any());
				verify(userRepository, never()).save(any(User.class));
			}
		}
	}

	@Nested
	@DisplayName("Describe: getMyInfo 메서드는")
	class Describe_getMyInfo {

		@Test
		@DisplayName("It: 이메일에 해당하는 회원 정보를 반환한다")
		void it_returns_user_info() {
			// given
			User user = createUser();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.getMyInfo(user.getEmail());

			// then
			assertThat(response.userId()).isEqualTo(1L);
			assertThat(response.userName()).isEqualTo(user.getUserName());
			assertThat(response.email()).isEqualTo(user.getEmail());
			assertThat(response.birthDate()).isEqualTo(user.getBirthDate());
			assertThat(response.gender()).isEqualTo(user.getGender());
			assertThat(response.role()).isEqualTo(user.getRole());
			assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber());
			assertThat(response.nationality()).isEqualTo(user.getNationality());
			assertThat(response.isAuthLinked()).isFalse();
		}

		@Test
		@DisplayName("It: 이메일에 해당하는 회원이 없으면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_user_dont_exists_when_user_does_not_exist() {
			// given
			String email = "legend@naver.com";
			given(userRepository.findByEmail(email)).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getMyInfo(email)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}

		@Test
		@DisplayName("It: 삭제된 회원이면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_USER_DONT_EXISTS_when_user_is_deleted() {
			// given
			User user = createUser();
			user.delete();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getMyInfo(user.getEmail())
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}
	}

	@Nested
	@DisplayName("Describe: getUserInfo 메서드는")
	class Describe_getUserInfo {

		@Test
		@DisplayName("It: id에 해당하는 회원 정보를 반환한다")
		void it_returns_user_info() {
			// given
			User user = createUser();
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.getUserInfo(user.getId());

			// then
			assertThat(response.userId()).isEqualTo(user.getId());
			assertThat(response.userName()).isEqualTo(user.getUserName());
			assertThat(response.email()).isEqualTo(user.getEmail());
			assertThat(response.birthDate()).isEqualTo(user.getBirthDate());
			assertThat(response.gender()).isEqualTo(user.getGender());
			assertThat(response.role()).isEqualTo(user.getRole());
			assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber());
			assertThat(response.nationality()).isEqualTo(user.getNationality());
			assertThat(response.isAuthLinked()).isFalse();
		}

		@Test
		@DisplayName("It: id에 해당하는 회원이 없으면 NO_USER 예외가 발생한다")
		void it_throws_no_user_when_user_does_not_exist() {
			// given
			Long userId = 1L;
			given(userRepository.findById(userId)).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getUserInfo(userId)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}

		@Test
		@DisplayName("It: 삭제된 회원이면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_USER_DONT_EXISTS_when_user_is_deleted() {
			// given
			User user = createUser();
			user.delete();
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.getUserInfo(user.getId())
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}
	}

	@Nested
	@DisplayName("Describe: updateMyInfo 메서드는")
	class Describe_updateMyInfo {

		@Test
		@DisplayName("It: 전달된 회원 정보를 수정하고 수정된 정보를 반환한다")
		void it_updates_user_info() {
			// given
			User user = createUser();
			UserUpdateRequest request = new UserUpdateRequest(
				"정수",
				null,
				null,
				"010-1111-2222",
				"CANADA"
			);

			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
			given(userRepository.existsByPhoneNumberAndEmailNot(request.phoneNumber(), user.getEmail()))
				.willReturn(false);

			// when
			UserInfoResponse response = userService.updateMyInfo(user.getEmail(), request);

			// then
			assertThat(response.userName()).isEqualTo("정수");
			assertThat(response.phoneNumber()).isEqualTo("010-1111-2222");
			assertThat(response.nationality()).isEqualTo("CANADA");
			assertThat(response.birthDate()).isEqualTo(user.getBirthDate());
			assertThat(response.gender()).isEqualTo(user.getGender());
		}

		@Test
		@DisplayName("It: 전화번호를 변경하지 않으면 중복 검사를 하지 않는다")
		void it_does_not_check_duplicate_when_phone_number_is_not_changed() {
			// given
			User user = createUser();
			UserUpdateRequest request = new UserUpdateRequest(
				"정수",
				null,
				null,
				user.getPhoneNumber(),
				null
			);

			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.updateMyInfo(user.getEmail(), request);

			// then
			assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber());
			verify(userRepository, never()).existsByPhoneNumberAndEmailNot(any(), any());
		}

		@Test
		@DisplayName("It: 변경할 전화번호가 이미 존재하면 USER_PHONE_ALREADY_EXISTS 예외가 발생한다")
		void it_throws_when_phone_number_already_exists() {
			// given
			User user = createUser();
			UserUpdateRequest request = new UserUpdateRequest(
				null,
				null,
				null,
				"010-1111-2222",
				null
			);

			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
			given(userRepository.existsByPhoneNumberAndEmailNot(request.phoneNumber(), user.getEmail()))
				.willReturn(true);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.updateMyInfo(user.getEmail(), request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_PHONE_ALREADY_EXISTS);
			assertThat(user.getPhoneNumber()).isEqualTo("010-1234-5678");
		}
	}

	@Nested
	@DisplayName("Describe: updateUserRole 메서드는")
	class Describe_updateUserRole {

		@Test
		@DisplayName("It: USER 역할을 ADMIN으로 변경한다")
		void it_changes_user_to_admin() {
			// given
			User user = createUser(Role.USER);
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.updateUserRole(user.getId(), request);

			// then
			assertThat(user.getRole()).isEqualTo(Role.ADMIN);
			assertThat(response.role()).isEqualTo(Role.ADMIN);
		}

		@Test
		@DisplayName("It: ADMIN 역할을 USER로 변경한다")
		void it_changes_admin_to_user() {
			// given
			User user = createUser(Role.ADMIN);
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.USER);
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			UserInfoResponse response = userService.updateUserRole(user.getId(), request);

			// then
			assertThat(user.getRole()).isEqualTo(Role.USER);
			assertThat(response.role()).isEqualTo(Role.USER);
		}

		@Test
		@DisplayName("It: id에 해당하는 회원이 없으면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_user_dont_exists_when_user_does_not_exist() {
			// given
			Long userId = 1L;
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			given(userRepository.findById(userId)).willReturn(Optional.empty());

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.updateUserRole(userId, request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}

		@Test
		@DisplayName("It: 삭제된 회원이면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_user_dont_exists_when_user_is_deleted() {
			// given
			User user = createUser();
			user.delete();
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.ADMIN);
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.updateUserRole(user.getId(), request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
		}

		@Test
		@DisplayName("It: SUPER_ADMIN으로 변경하려 하면 USER_ROLE_CHANGE_NOT_ALLOWED 예외가 발생한다")
		void it_throws_when_role_change_is_not_allowed() {
			// given
			User user = createUser(Role.USER);
			UserRoleUpdateRequest request = new UserRoleUpdateRequest(Role.SUPER_ADMIN);
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.updateUserRole(user.getId(), request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_ROLE_CHANGE_NOT_ALLOWED);
			assertThat(user.getRole()).isEqualTo(Role.USER);
		}
	}

	@Nested
	@DisplayName("Describe: changePassword 메서드는")
	class Describe_changePassword {

		@Test
		@DisplayName("It: 현재 비밀번호가 일치하면 새 비밀번호로 변경한다")
		void it_changes_password() {
			// given
			User user = createUser();
			PasswordChangeRequest request = new PasswordChangeRequest("password123", "newPassword123");

			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.currentPassword(), user.getPassword())).willReturn(true);
			given(passwordEncoder.encode(request.newPassword())).willReturn("newEncodedPassword");

			// when
			userService.changePassword(user.getEmail(), request);

			// then
			assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
		}

		@Test
		@DisplayName("It: 현재 비밀번호가 일치하지 않으면 USER_PASSWORD_NOT_MATCH 예외가 발생한다")
		void it_throws_when_current_password_does_not_match() {
			// given
			User user = createUser();
			PasswordChangeRequest request = new PasswordChangeRequest("wrongPassword", "newPassword123");

			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.currentPassword(), user.getPassword())).willReturn(false);

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.changePassword(user.getEmail(), request)
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_PASSWORD_NOT_MATCH);
			verify(passwordEncoder, never()).encode(request.newPassword());
		}
	}

	@Nested
	@DisplayName("Describe: deleteMyAccount 메서드는")
	class Describe_deleteMyAccount {

		@Test
		@DisplayName("It: 회원을 탈퇴 처리하고 리프레시 토큰을 삭제한다")
		void it_deletes_user_and_refresh_tokens() {
			// given
			User user = createUser();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			userService.deleteMyAccount(user.getEmail());

			// then
			assertThat(user.getDeletedAt()).isNotNull();
			verify(refreshTokenRepository).deleteByEmail(user.getEmail());
		}

		@Test
		@DisplayName("It: 이미 삭제된 회원이면 USER_DONT_EXISTS 예외가 발생한다")
		void it_throws_user_dont_exists_when_user_is_deleted() {
			// given
			User user = createUser();
			user.delete();
			given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

			// when
			BusinessException exception = assertThrows(
				BusinessException.class,
				() -> userService.deleteMyAccount(user.getEmail())
			);

			// then
			assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_DONT_EXISTS);
			verify(refreshTokenRepository, never()).deleteByEmail(user.getEmail());
		}
	}

	private SignupRequest createSignupRequest() {
		return new SignupRequest(
			"대정수",
			"legend@naver.com",
			"password123",
			LocalDate.of(2000, 1, 1),
			Gender.MALE,
			"010-1234-5678",
			"KOREA"
		);
	}

	private User createUser() {
		return createUser(Role.USER);
	}

	private User createUser(Role role) {
		User user = new User(
			"대정수",
			"legend@naver.com",
			"encodedPassword",
			LocalDate.of(2000, 1, 1),
			role,
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
}
