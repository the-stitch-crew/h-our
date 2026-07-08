package stitch.crew.hour.policy.dto;

import stitch.crew.hour.policy.domain.WeekDay;

import java.time.LocalTime;
import java.util.Set;

public record LessonPolicyResponse(
        Integer reservationAvailableDays,
        Integer reservationDeadlineDays,
        Integer cancelDeadlineDays,
        Integer depositAmount,
        LocalTime startTime,
        LocalTime endTime,
        Set<WeekDay> regularDays
) {
}
