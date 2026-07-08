package stitch.crew.hour.policy.dto;

import jakarta.validation.constraints.NotNull;
import stitch.crew.hour.policy.domain.WeekDay;

import java.time.LocalTime;
import java.util.Set;

public record LessonPolicyRequest(
        @NotNull(message = "예약 가능 기간은 필수값입니다.")
        Integer reservationAvailableDays,
        @NotNull(message = "최소 예약 가능 기간은 필수값입니다.")
        Integer reservationDeadlineDays,
        @NotNull(message = "최소 예약 취소 기간은 필수값입니다.")
        Integer cancelDeadlineDays,
        @NotNull(message = "예약금액은 필수값입니다.")
        Integer depositAmount,
        @NotNull(message = "오픈 시간은 필수값입니다.")
        LocalTime startTime,
        @NotNull(message = "마감 시간은 필수값입니다.")
        LocalTime endTime,
        @NotNull(message = "정기 휴무일은 필수값입니다.")
        Set<WeekDay> regularDays
) {
}
