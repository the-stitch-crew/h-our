package stitch.crew.hour.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.address.repository.AddressRepository;
import stitch.crew.hour.auth.repository.RefreshTokenRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.dto.MyPageResponse;
import stitch.crew.hour.user.dto.PasswordChangeRequest;
import stitch.crew.hour.user.dto.SignupRequest;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.dto.UserInfoResponse;
import stitch.crew.hour.user.dto.SignupResponse;
import stitch.crew.hour.user.dto.UserUpdateRequest;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AddressRepository addressRepository;

	@Transactional
	public SignupResponse signup(SignupRequest request){

		PreConditions.validate(
			!userRepository.existsByEmail(request.email()),
			ErrorCode.USER_EMAIL_ALREADY_EXISTS
		);

		PreConditions.validate(
			!userRepository.existsByPhoneNumber(request.phoneNumber()),
			ErrorCode.USER_PHONE_ALREADY_EXISTS
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

	public UserInfoResponse getMyInfo(String email) {
		return UserInfoResponse.from(getActiveUser(email));
	}

	public MyPageResponse getMyPage(String email) {
		User user = getActiveUser(email);
		return MyPageResponse.from(
			user,
			addressRepository.findAllByUserIdAndDeletedAtIsNullOrderByIsMainDescCreatedAtDesc(user.getId())
		);
	}

	@Transactional
	public UserInfoResponse updateMyInfo(String email, UserUpdateRequest request) {
		User user = getActiveUser(email);

		if (request.phoneNumber() != null && !request.phoneNumber().equals(user.getPhoneNumber())) {
			PreConditions.validate(
				!userRepository.existsByPhoneNumberAndEmailNot(request.phoneNumber(), email),
				ErrorCode.USER_PHONE_ALREADY_EXISTS
			);
		}

		user.updateProfile(
			request.userName(),
			request.birthDate(),
			request.gender(),
			request.phoneNumber(),
			request.nationality()
		);

		return UserInfoResponse.from(user);
	}

	@Transactional
	public void changePassword(String email, PasswordChangeRequest request) {
		User user = getActiveUser(email);

		PreConditions.validate(
			passwordEncoder.matches(request.currentPassword(), user.getPassword()),
			ErrorCode.USER_PASSWORD_NOT_MATCH
		);

		user.changePassword(passwordEncoder.encode(request.newPassword()));
	}

	@Transactional
	public void deleteMyAccount(String email) {
		User user = getActiveUser(email);
		user.delete();
		refreshTokenRepository.deleteByEmail(email);
	}

	private User getActiveUser(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_DONT_EXISTS));

		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.USER_DONT_EXISTS
		);

		return user;
	}

	public CurrentUser loadCurrentUserByEmail(String email) {
		return CurrentUser.from(getActiveUser(email));
	}

	public User getActiveUserFromCurrentUser(CurrentUser currentUser){
		PreConditions.validate(
			currentUser != null,
			ErrorCode.UNAUTHORIZED
		);

		User user = userRepository.findByIdOrthrow(currentUser.getId());

		PreConditions.validate(
			user.getDeletedAt() == null,
			ErrorCode.USER_DONT_EXISTS
		);

		return user;
	}

}
