package stitch.crew.hour.lesson.dto;

import jakarta.validation.constraints.*;

public record LessonRequest(
        @NotEmpty(message = "이름은 필수값입니다.")
        String name,

        @NotNull(message = "가격은 필수값입니다.")
        @Min(value = 0, message = "가격은 0원 이상입니다.")
        @Max(value = 1000000, message = "가격은 백만원 이하입니다.")
        Integer price,

        @NotNull(message = "수업시간은 필수값입니다.")
        @Min(value = 1, message = "수업시간은 1시간 이상입니다.")
        @Max(value = 12, message = "수업시간은 12시간 이하입니다.")
        Integer duration
) {
}
