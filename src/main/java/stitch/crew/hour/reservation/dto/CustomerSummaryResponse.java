package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.user.domain.User;

import java.time.LocalDate;

public record CustomerSummaryResponse(
	Long userId,
	String userName,
	String email,
	String phoneNumber,
	String nationality,
	Integer reservationCount,
	Integer visitCount,
	LocalDate lastVisitDate
) {

	public static CustomerSummaryResponse from(User user,  int reservationCount, int visitCount,LocalDate lastVisitDate) {
		return new CustomerSummaryResponse(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getNationality(),
			reservationCount,
			visitCount,
			lastVisitDate
		);
	}
}
