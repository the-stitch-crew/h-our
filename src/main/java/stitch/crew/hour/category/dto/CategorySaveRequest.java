package stitch.crew.hour.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategorySaveRequest(
        @NotEmpty(message = "name은 필수값입니다.")
        @Size(max = 20, message = "name은 최대 20자입니다.")
        String name,

        @NotNull(message = "썸네일은 null이 허용되지 않습니다.")
        String thumbnail
) {
}
