package stitch.crew.hour.policy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.policy.dto.DeliveryFeeResponse;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/api/shppingpolicy")
public class ShippingPolicyContoller {

    private final ShippingPolicyRepository repository;

    @GetMapping
    public ResponseEntity<ApiResponses<DeliveryFeeResponse>> getShippingFee(){
        ShippingPolicy policy = repository.findActiveOrThrow();
        return ApiResult.ok(
                SuccessCode.DELIVERY_FEE_READED_SUCCESS,
                new DeliveryFeeResponse(
                        policy.getDeliveryFee() + policy.getExtraFee()
                )
        );
    }

}
