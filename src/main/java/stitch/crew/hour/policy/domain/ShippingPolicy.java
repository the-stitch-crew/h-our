package stitch.crew.hour.policy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;

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
        this.isActive = isActive;
    }
}
