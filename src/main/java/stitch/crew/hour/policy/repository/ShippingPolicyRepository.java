package stitch.crew.hour.policy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;

import java.util.List;
import java.util.Optional;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {
    Optional<ShippingPolicy> findFirstByIsActiveTrueAndDeletedAtIsNullOrderByIdDesc();

    List<ShippingPolicy> findAllByIsActiveTrueAndDeletedAtIsNull();

    List<ShippingPolicy> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    Optional<ShippingPolicy> findByIdAndDeletedAtIsNull(Long id);

    default Optional<ShippingPolicy> findActive() {
        return findFirstByIsActiveTrueAndDeletedAtIsNullOrderByIdDesc();
    }

    default ShippingPolicy findActiveOrThrow(){
        return findActive().orElseThrow(
                ()-> new BusinessException(ErrorCode.NO_SHIPPING_POLICY)
        );
    }

    default ShippingPolicy findByIdOrThrow(Long id) {
        return findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new BusinessException(ErrorCode.SHIPPING_POLICY_NOT_FOUND)
        );
    }
}
