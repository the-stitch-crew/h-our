package stitch.crew.hour.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CategorySaveRequest(
        @NotEmpty
        @Size(max = 20)
        String name,

        String thumbnail
) {
}
