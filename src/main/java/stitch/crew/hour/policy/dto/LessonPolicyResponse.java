package stitch.crew.hour.policy.dto;

import stitch.crew.hour.policy.domain.LessonPolicy;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

public record LessonPolicyResponse(
        Integer reservationAvailableDays,
        Integer reservationDeadlineDays,
        Integer cancelDeadlineDays,
        Integer depositAmount,
        LocalTime startTime,
        LocalTime endTime,
        Set<DayOfWeek> regularDays
) {
    public static LessonPolicyResponse from(LessonPolicy policy) {
        return new LessonPolicyResponse(
                policy.getReservationAvailableDays(),
                policy.getReservationDeadlineDays(),
                policy.getCancelDeadlineDays(),
                policy.getDepositAmount(),
                policy.getStartTime(),
                policy.getEndTime(),
                policy.getRegularDays()
        );
    }
}
