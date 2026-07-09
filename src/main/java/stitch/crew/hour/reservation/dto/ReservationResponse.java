package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.reservation.domain.Reservation;
import stitch.crew.hour.reservation.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer deposit,
        Integer price,
        String request,
        UUID reservationNumber,
        ReservationStatus status,
        LessonResponse lesson

) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getDate(), r.getStartTime(), r.getEndTime(), r.getDeposit(), r.getPrice(),
                r.getRequest(), r.getReservationNumber(), r.getStatus(), LessonResponse.from(r.getLesson()));
    }
}
