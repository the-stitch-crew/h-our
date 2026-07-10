package stitch.crew.hour.policy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ShippingPolicyCreateRequest(
        @NotNull(message = "배송비는 필수값입니다.")
        @Min(value = 0, message = "배송비는 0원 이상입니다.")
        Long deliveryFee,

        @Min(value = 0, message = "추가 배송비는 0원 이상입니다.")
        Long extraFee,

        Boolean isActive
) {
}
