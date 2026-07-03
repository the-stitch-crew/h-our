package stitch.crew.hour.shippingpolicy.domain;

import jakarta.persistence.*;
import stitch.crew.hour.common.domain.BaseEntity;

@Entity
public class ShippingPolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long deliveryFee;

    private Long extraFee;

    private Boolean isActive;
}
