package stitch.crew.hour.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateCartProductRequest(
        @Schema(
                description = "수정할 장바구니 내 상품 ID",
                example = "1"
        )
        @NotNull Long cartProductId,
        @Schema(
                description = "수정할 장바구니 내 상품 수",
                example = "1"
        )
        @NotNull Long amount,
        @Schema(
                description = "주문의 상품 옵션"
        )
        String option
) {
}
