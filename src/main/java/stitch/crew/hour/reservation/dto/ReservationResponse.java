package stitch.crew.hour.reservation.dto;

import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.reservation.domain.ReservationState;

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
        ReservationState state,
        LessonResponse lesson

) {
}
