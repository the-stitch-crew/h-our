package stitch.crew.hour.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignupResponse signup(SignupRequest request){

		PreConditions.validate(
			!userRepository.existsByEmail(request.email()),
			ErrorCode.USER_EMAIL_ALREADY_EXISTS
		);

		PreConditions.validate(
			!userRepository.existsByPhoneNumber(request.phoneNumber()),
			ErrorCode.USER_EMAIL_ALREADY_EXISTS
		);

		User user = new User(
			request.userName(),
			request.email(),
			passwordEncoder.encode(request.password()),
			request.birthDate(),
			Role.USER,
			request.gender(),
			null,
			request.phoneNumber(),
			request.nationality(),
			false,
			false
		);

		User savedUser = userRepository.save(user);

		return new SignupResponse(savedUser.getId());
	}


}
