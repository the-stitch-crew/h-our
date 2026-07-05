package stitch.crew.hour.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.domain.RefreshToken;
import stitch.crew.hour.auth.dto.LoginRequest;
import stitch.crew.hour.auth.dto.RefreshTokenRequest;
import stitch.crew.hour.auth.dto.TokenBody;
import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.auth.dto.LoginResponse;
import stitch.crew.hour.common.util.PreConditions;
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

	@Transactional
	public LoginResponse login(LoginRequest request) {
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
	public LoginResponse refresh(RefreshTokenRequest request) {
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
