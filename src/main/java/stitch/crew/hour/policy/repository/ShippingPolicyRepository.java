package stitch.crew.hour.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;

import java.util.Optional;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {
    @Query(
            """
            SELECT sp
                 FROM ShippingPolicy sp
                 WHERE sp.isActive = true
            """
    )
    Optional<ShippingPolicy> findActive();

    default ShippingPolicy findActiveOrThrow(){
        return findActive().orElseThrow(
                ()-> new BusinessException(ErrorCode.NO_SHIPPING_POLICY)
        );
    }

    @Modifying
    @Query(
            """
            UPDATE ShippingPolicy sp
               SET sp.isActive = false
            """
    )
    void setAllUnActive();
}
