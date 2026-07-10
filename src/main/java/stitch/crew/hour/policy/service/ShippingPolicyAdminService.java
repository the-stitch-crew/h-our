package stitch.crew.hour.policy.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.dto.ShippingPolicyCreateRequest;
import stitch.crew.hour.policy.dto.ShippingPolicyResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingPolicyAdminService {
    private final ShippingPolicyRepository shippingPolicyRepository;

    @Transactional(readOnly = true)
    public List<ShippingPolicyResponse> getShippingPolicies() {
        return shippingPolicyRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(ShippingPolicyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShippingPolicyResponse getShippingPolicy(Long shippingPolicyId) {
        ShippingPolicy policy = findByIdOrThrow(shippingPolicyId);
        return ShippingPolicyResponse.from(policy);
    }

    @Transactional
    public ShippingPolicyResponse createShippingPolicy(@Valid ShippingPolicyCreateRequest request) {
        ShippingPolicy policy = new ShippingPolicy(
                request.deliveryFee(),
                request.extraFee(),
                false
        );

        if (Boolean.TRUE.equals(request.isActive())) {
            deactivateActivePoliciesExcept(null);
            policy.activate();
        }

        return ShippingPolicyResponse.from(shippingPolicyRepository.save(policy));
    }

    @Transactional
    public ShippingPolicyResponse updateShippingPolicy(
            Long shippingPolicyId,
            @Valid ShippingPolicyUpdateRequest request
    ) {
        ShippingPolicy policy = findByIdOrThrow(shippingPolicyId);
        policy.update(request);

        if (request.isActive() != null) {
            if (request.isActive()) {
                activatePolicy(policy);
            } else {
                policy.deactivate();
            }
        }

        return ShippingPolicyResponse.from(policy);
    }

    @Transactional
    public ShippingPolicyResponse activateShippingPolicy(Long shippingPolicyId) {
        ShippingPolicy policy = findByIdOrThrow(shippingPolicyId);
        activatePolicy(policy);
        return ShippingPolicyResponse.from(policy);
    }

    @Transactional
    public void deleteShippingPolicy(Long shippingPolicyId) {
        ShippingPolicy policy = findByIdOrThrow(shippingPolicyId);
        policy.deactivate();
        policy.delete();
    }

    private ShippingPolicy findByIdOrThrow(Long shippingPolicyId) {
        return shippingPolicyRepository.findByIdAndDeletedAtIsNull(shippingPolicyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPPING_POLICY_NOT_FOUND));
    }

    private void activatePolicy(ShippingPolicy policy) {
        deactivateActivePoliciesExcept(policy.getId());
        policy.activate();
    }

    private void deactivateActivePoliciesExcept(Long shippingPolicyId) {
        shippingPolicyRepository.findAllByIsActiveTrueAndDeletedAtIsNull()
                .stream()
                .filter(policy -> shippingPolicyId == null || !shippingPolicyId.equals(policy.getId()))
                .forEach(ShippingPolicy::deactivate);
    }
}
