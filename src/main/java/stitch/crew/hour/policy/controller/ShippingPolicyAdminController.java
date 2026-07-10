package stitch.crew.hour.policy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.dto.DeliveryFeeRequest;
import stitch.crew.hour.policy.dto.DeliveryFeeResponse;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.domain.CurrentUser;

@Slf4j
@RestController
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Transactional
@RequestMapping("/api/admin/shippingpolicy")
public class ShippingPolicyAdminController {

    private final ShippingPolicyRepository shippingPolicyRepository;

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponses<DeliveryFeeResponse>> createShippingPolicy(
            @RequestBody DeliveryFeeRequest request
    ){
        shippingPolicyRepository.setAllUnActive();

        ShippingPolicy saved = shippingPolicyRepository.save(
                new ShippingPolicy(
                        request.deliveryFee().longValue(),
                        request.extraFee().longValue(),
                        true
                )
        );

        return ApiResult.created(
            SuccessCode.DELIVERY_FEE_SAVEED_SUCCESS,
            new DeliveryFeeResponse(saved.getDeliveryFee() + saved.getExtraFee())
        );
    }

}
