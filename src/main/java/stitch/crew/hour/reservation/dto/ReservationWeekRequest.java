package stitch.crew.hour.reservation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReservationWeekRequest(
        @Min(value = 2020, message = "년도는 2020년 이전은 불가합니다.")
        Integer year,
        @Min(value = 1, message = "월은 1월부터 입니다.")
        @Max(value = 12, message = "월은 12월까지 입니다.")
        Integer month,
        @Min(value = 1, message = "주는 1부터 가능합니다.")
        @Max(value = 5, message = "주는 5까지 가능합니다.")
        Integer week
) {
}
