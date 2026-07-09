package stitch.crew.hour.user.dto;

import java.time.LocalDateTime;

import stitch.crew.hour.user.domain.User;

public record AdminUserSearchResponse(
	Long userId,
	String userName,
	String email,
	String phoneNumber,
	String role,
	String gender,
	String nationality,
	Boolean isAuthLinked,
	Boolean blacklisted,
	LocalDateTime createdAt,
	LocalDateTime deletedAt
) {
	public static AdminUserSearchResponse from(User user) {
		return new AdminUserSearchResponse(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getRole().name(),
			user.getGender().name(),
			user.getNationality(),
			user.getIsAuthLinked(),
			user.getIdBlack(),
			user.getCreatedAt(),
			user.getDeletedAt()
		);
	}
}
