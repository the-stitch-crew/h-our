package stitch.crew.hour.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.dto.LoginRequest;
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
}
