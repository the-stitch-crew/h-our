package stitch.crew.hour.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @Schema(
                examples = "대홍단 왕감자",
                description = "상품 명칭",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 50
        )
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 50, message = "상품명은 50자까지 입력가능합니다.") String name,
        @Schema(
                examples = "5000",
                description = "상품 가격",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "상품 가격은 필수입니다.")
        @Positive(message = "상품 가격은 0보다 커야 합니다.") Long price,
        @Schema(
                examples = "위대한 령도자님의 기운이 담기신 감자입니다.",
                description = "상품 요약"
        )
        String summary,
        @Schema(
                examples = "감자 감자 왕감자 참말 참말 좋아요",
                description = "상품 내용"
        )
        String description,
        @Schema(
                examples = "구황작물",
                description = "카테고리 번호",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull Long categoryId
) {
}
