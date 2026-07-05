package stitch.crew.hour.user.dto;

import java.time.LocalDate;

import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;

public record UserInfoResponse(
	Long userId,
	String userName,
	String email,
	LocalDate birthDate,
	Gender gender,
	Role role,
	String phoneNumber,
	String nationality,
	Boolean isAuthLinked
) {

	public static UserInfoResponse from(User user) {
		return new UserInfoResponse(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getBirthDate(),
			user.getGender(),
			user.getRole(),
			user.getPhoneNumber(),
			user.getNationality(),
			user.getIsAuthLinked()
		);
	}
}
