package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.user.domain.User;

import java.time.LocalDate;

public record CustomerSummaryResponse(
	Long userId,
	String userName,
	String email,
	String phoneNumber,
	String nationality,
	Integer visitCount,
	Integer reservationCount,
	LocalDate lastVisitDate
) {

	public static CustomerSummaryResponse from(User user, int visitCount, int reservationCount, LocalDate lastVisitDate) {
		return new CustomerSummaryResponse(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getNationality(),
			visitCount,
			reservationCount,
			lastVisitDate
		);
	}
}
