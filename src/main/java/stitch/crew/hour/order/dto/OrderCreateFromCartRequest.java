package stitch.crew.hour.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;

import java.util.List;

public record OrderCreateFromCartRequest(
        @Schema(
                examples = "가상융합기술아카데미",
                description = "상품을 수신할 주소",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String address,

        @Schema(
                examples = "26331",
                description = "상품을 수신할 주소의 우편번호",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String postalCode,

        @Schema(
                examples = "이정수",
                description = "수신자 성명"
        )
        String receiverName,

        @Schema(
                examples = "감자를 잘 포장해주세요.",
                description = "요청사항"
        )
        String request,

        @Schema(
                examples = "010-1234-5678",
                description = "수신자 전화번호",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Pattern(
                regexp = "^01([016789])-?\\d{3,4}-?\\d{4}$",
                message = "올바른 휴대폰 번호 형식이 아닙니다."
        )
        @NotBlank String receiverPhoneNumber
) {
}
