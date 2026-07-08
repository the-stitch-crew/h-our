package stitch.crew.hour.policy.dto;

import jakarta.validation.constraints.NotNull;
import stitch.crew.hour.policy.domain.WeekDay;

import java.time.LocalTime;
import java.util.Set;

public record LessonPolicyRequest(
        @NotNull
        Integer reservationAvailableDays,
        @NotNull
        Integer reservationDeadlineDays,
        @NotNull
        Integer cancelDeadlineDays,
        @NotNull
        Integer depositAmount,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime,
        @NotNull
        Set<WeekDay> regularDays
) {
}
