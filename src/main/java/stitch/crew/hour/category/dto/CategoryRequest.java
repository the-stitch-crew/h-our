package stitch.crew.hour.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotEmpty(message = "name은 필수값입니다.")
        @Size(max = 20, message = "name은 최대 20자입니다.")
        String name
) {
}
