package stitch.crew.hour.auth.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.domain.RefreshToken;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.dto.OAuthSignupInfoResponse;
import stitch.crew.hour.auth.dto.OAuthSignupPayload;
import stitch.crew.hour.auth.dto.OAuthSignupRequest;
import stitch.crew.hour.auth.dto.RefreshTokenRequest;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.auth.dto.KeyPair;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final SignupTokenStore signupTokenStore;

	@Transactional
	public KeyPair login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

		PreConditions.validate(
			passwordEncoder.matches(request.password(), user.getPassword()),
			ErrorCode.LOGIN_FAILED
		);

		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.ALREADY_DELETED
		);

		return jwtTokenProvider.issueKeyPair(user.getEmail(), user.getRole());
	}

	@Transactional
	public KeyPair oauthSignup(
		String signupToken,
		OAuthSignupRequest request
	) {
		OAuthSignupPayload signupPayload = findSignupPayload(signupToken);

		String provider = signupPayload.provider().toUpperCase(Locale.ROOT);

		PreConditions.validate(
			provider.equals("GOOGLE"),
			ErrorCode.VALIDATION_FAILED
		);

		//필터체인에서 기존 회원이 있으면 이 메서드를 실행시키지 않음. 신규회원의 상황인데 이메일이 있으면 안되니까 예외.
		PreConditions.validate(
			!userRepository.existsByEmail(signupPayload.email()),
			ErrorCode.USER_EMAIL_ALREADY_EXISTS
		);

		PreConditions.validate(
			!userRepository.existsByPhoneNumber(request.phoneNumber()),
			ErrorCode.USER_PHONE_ALREADY_EXISTS
		);

		User user = new User(
			signupPayload.userName(),
			signupPayload.email(),
			passwordEncoder.encode(UUID.randomUUID().toString()),
			request.birthDate(),
			Role.USER,
			request.gender(),
			provider,
			request.phoneNumber(),
			request.nationality(),
			true,
			false
		);

		User savedUser = userRepository.save(user);
		KeyPair keyPair = jwtTokenProvider.issueKeyPair(savedUser.getEmail(), savedUser.getRole());
		signupTokenStore.delete(signupToken);
		return keyPair;
	}

	public OAuthSignupInfoResponse getOAuthSignupInfo(String signupToken) {
		OAuthSignupPayload signupPayload = findSignupPayload(signupToken);

		return OAuthSignupInfoResponse.from(signupPayload);
	}

	private OAuthSignupPayload findSignupPayload(String signupToken) {
		if (!StringUtils.hasText(signupToken)) {
			throw new BusinessException(ErrorCode.INVALID_SIGNUP_TOKEN);
		}

		return signupTokenStore.find(signupToken)
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_SIGNUP_TOKEN));
	}

	@Transactional
	public KeyPair refresh(RefreshTokenRequest request) {
		String requestRefreshToken = request.refreshToken();
		jwtTokenProvider.validateRefreshToken(requestRefreshToken);

		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(requestRefreshToken)
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

		TokenBody tokenBody = jwtTokenProvider.parseJwt(requestRefreshToken);
		PreConditions.validate(
			refreshToken.getEmail().equals(tokenBody.getEmail()),
			ErrorCode.INVALID_REFRESH_TOKEN
		);

		User user = userRepository.findByEmail(tokenBody.getEmail())
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_DONT_EXISTS));

		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.USER_DONT_EXISTS
		);

		refreshTokenRepository.delete(refreshToken);
		return jwtTokenProvider.issueKeyPair(user.getEmail(), user.getRole());
	}

	@Transactional
	public void logout(RefreshTokenRequest request) {
		String requestRefreshToken = request.refreshToken();
		jwtTokenProvider.validateRefreshToken(requestRefreshToken);

		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(requestRefreshToken)
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

		refreshTokenRepository.delete(refreshToken);
	}
}
