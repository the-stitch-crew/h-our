package stitch.crew.hour.policy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingPolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deliveryFee;

    private Long extraFee;

    private Boolean isActive;

    public ShippingPolicy(
        Long deliveryFee,
        Long extraFee,
        Boolean isActive
    ){
        this.deliveryFee = deliveryFee;
        this.extraFee = extraFee;
        this.isActive = Boolean.TRUE.equals(isActive);
    }

    public void update(ShippingPolicyUpdateRequest request) {
        this.deliveryFee = request.deliveryFee();
        this.extraFee = request.extraFee();
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
