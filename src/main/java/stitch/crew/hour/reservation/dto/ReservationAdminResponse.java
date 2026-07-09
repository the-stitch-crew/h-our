package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.reservation.domain.Reservation;
import stitch.crew.hour.reservation.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationAdminResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer deposit,
        Integer price,
        String request,
        UUID reservationNumber,
        ReservationStatus state,
        CustomerSummaryResponse customer,
        LessonResponse lesson

) {
    public static ReservationAdminResponse from(Reservation reservation, CustomerSummaryResponse customer, Lesson lesson) {
        return new ReservationAdminResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getDeposit(),
                reservation.getPrice(),
                reservation.getRequest() == null? null: reservation.getRequest(),
                reservation.getReservationNumber(),
                reservation.getStatus(),
                customer,
                LessonResponse.from(lesson)
        );
    }
}
