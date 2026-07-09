package stitch.crew.hour.policy.dto;

import jakarta.validation.constraints.NotNull;

public record DeliveryFeeRequest(
        @NotNull Integer deliveryFee,
        Integer extraFee
        ) {
}
