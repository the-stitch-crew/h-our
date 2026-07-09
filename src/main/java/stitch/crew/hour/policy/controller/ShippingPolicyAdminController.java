package stitch.crew.hour.policy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.dto.ShippingPolicyCreateRequest;
import stitch.crew.hour.policy.dto.ShippingPolicyResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;
import stitch.crew.hour.policy.service.ShippingPolicyAdminService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/shipping-policies")
public class ShippingPolicyAdminController {
    private final ShippingPolicyAdminService shippingPolicyAdminService;

    @GetMapping
    public ResponseEntity<ApiResponses<List<ShippingPolicyResponse>>> getShippingPolicies() {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_READ,
                shippingPolicyAdminService.getShippingPolicies()
        );
    }

    @GetMapping("/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> getShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_READ,
                shippingPolicyAdminService.getShippingPolicy(shippingPolicyId)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> createShippingPolicy(
            @RequestBody @Valid ShippingPolicyCreateRequest request
    ) {
        return ApiResult.created(
                SuccessCode.SHIPPING_POLICY_CREATED,
                shippingPolicyAdminService.createShippingPolicy(request)
        );
    }

    @PutMapping("/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> updateShippingPolicy(
            @PathVariable Long shippingPolicyId,
            @RequestBody @Valid ShippingPolicyUpdateRequest request
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_UPDATED,
                shippingPolicyAdminService.updateShippingPolicy(shippingPolicyId, request)
        );
    }

    @PatchMapping("/{shippingPolicyId}/active")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> activateShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_ACTIVATED,
                shippingPolicyAdminService.activateShippingPolicy(shippingPolicyId)
        );
    }

    @DeleteMapping("/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<Void>> deleteShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        shippingPolicyAdminService.deleteShippingPolicy(shippingPolicyId);
        return ApiResult.ok(SuccessCode.SHIPPING_POLICY_DELETED);
    }
}
