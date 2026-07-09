package stitch.crew.hour.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import stitch.crew.hour.user.domain.User;

public record AdminUserDetailResponse(
	Long userId,
	String userName,
	String email,
	String phoneNumber,
	LocalDate birthDate,
	String role,
	String gender,
	String nationality,
	String provider,
	Boolean isAuthLinked,
	Boolean blacklisted,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	LocalDateTime deletedAt
) {
	public static AdminUserDetailResponse from(User user) {
		return new AdminUserDetailResponse(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getBirthDate(),
			user.getRole().name(),
			user.getGender().name(),
			user.getNationality(),
			user.getProvider(),
			user.getIsAuthLinked(),
			user.getIdBlack(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			user.getDeletedAt()
		);
	}
}
