package stitch.crew.hour.policy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.dto.DeliveryFeeRequest;
import stitch.crew.hour.policy.dto.DeliveryFeeResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyCreateRequest;
import stitch.crew.hour.policy.dto.ShippingPolicyResponse;
import stitch.crew.hour.policy.dto.ShippingPolicyUpdateRequest;
import stitch.crew.hour.policy.service.ShippingPolicyAdminService;

import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ShippingPolicyAdminController {

    private final ShippingPolicyAdminService shippingPolicyAdminService;

    @GetMapping("/shipping-policies")
    public ResponseEntity<ApiResponses<List<ShippingPolicyResponse>>> getShippingPolicies() {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_READ,
                shippingPolicyAdminService.getShippingPolicies()
        );
    }

    @GetMapping("/shipping-policies/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> getShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_READ,
                shippingPolicyAdminService.getShippingPolicy(shippingPolicyId)
        );
    }

    @PostMapping("/shipping-policies")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> createShippingPolicy(
            @Valid @RequestBody ShippingPolicyCreateRequest request
    ) {
        return ApiResult.created(
                SuccessCode.SHIPPING_POLICY_CREATED,
                shippingPolicyAdminService.createShippingPolicy(request)
        );
    }

    @PutMapping("/shipping-policies/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> updateShippingPolicy(
            @PathVariable Long shippingPolicyId,
            @Valid @RequestBody ShippingPolicyUpdateRequest request
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_UPDATED,
                shippingPolicyAdminService.updateShippingPolicy(shippingPolicyId, request)
        );
    }

    @PatchMapping("/shipping-policies/{shippingPolicyId}/active")
    public ResponseEntity<ApiResponses<ShippingPolicyResponse>> activateShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        return ApiResult.ok(
                SuccessCode.SHIPPING_POLICY_ACTIVATED,
                shippingPolicyAdminService.activateShippingPolicy(shippingPolicyId)
        );
    }

    @DeleteMapping("/shipping-policies/{shippingPolicyId}")
    public ResponseEntity<ApiResponses<Void>> deleteShippingPolicy(
            @PathVariable Long shippingPolicyId
    ) {
        shippingPolicyAdminService.deleteShippingPolicy(shippingPolicyId);
        return ApiResult.ok(SuccessCode.SHIPPING_POLICY_DELETED);
    }

    @PostMapping("/shippingpolicy")
    public ResponseEntity<ApiResponses<DeliveryFeeResponse>> createDeliveryFeePolicy(
            @RequestBody DeliveryFeeRequest request
    ){
        ShippingPolicyResponse saved = shippingPolicyAdminService.createShippingPolicy(
                new ShippingPolicyCreateRequest(
                        request.deliveryFee().longValue(),
                        request.extraFee().longValue(),
                        true
                )
        );

        return ApiResult.created(
            SuccessCode.DELIVERY_FEE_SAVEED_SUCCESS,
            new DeliveryFeeResponse(saved.deliveryFee() + saved.extraFee())
        );
    }

}
