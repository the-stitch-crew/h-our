package stitch.crew.hour.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddCartProductRequest(
       @Schema(
            description = "추가할 상품 ID",
            example = "1"
       )
       @NotNull Long productId,
       @Schema(
               description = "추가할 상품 수",
               example = "1"
       )
       @NotNull Long amount
) {
}
