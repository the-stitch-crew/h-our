package stitch.crew.hour.policy.dto;

import stitch.crew.hour.policy.domain.ShippingPolicy;

import java.time.LocalDateTime;

public record ShippingPolicyResponse(
        Long shippingPolicyId,
        Long deliveryFee,
        Long extraFee,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ShippingPolicyResponse from(ShippingPolicy policy) {
        return new ShippingPolicyResponse(
                policy.getId(),
                policy.getDeliveryFee(),
                policy.getExtraFee(),
                policy.getIsActive(),
                policy.getCreatedAt(),
                policy.getUpdatedAt(),
                policy.getDeletedAt()
        );
    }
}
