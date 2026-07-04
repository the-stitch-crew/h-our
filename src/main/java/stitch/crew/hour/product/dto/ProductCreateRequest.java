package stitch.crew.hour.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @Size(max = 50, message = "상품명은 50자까지 입력가능합니다.") String name,
        @NotNull Long price,
        String summary,
        String description
) {
}
