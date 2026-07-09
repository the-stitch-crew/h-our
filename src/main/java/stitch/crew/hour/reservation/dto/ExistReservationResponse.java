package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.reservation.domain.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ExistReservationResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
    public static ExistReservationResponse from(Reservation reservation) {
        return new ExistReservationResponse(reservation.getId(), reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
    }
}
