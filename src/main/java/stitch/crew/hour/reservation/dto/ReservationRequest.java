package stitch.crew.hour.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequest(
        @NotNull
        LocalDate date,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime,
        @NotNull
        Integer deposit,
        @NotNull
        Integer price,
        String request,
        @NotNull
        Long lessonId) {
}
