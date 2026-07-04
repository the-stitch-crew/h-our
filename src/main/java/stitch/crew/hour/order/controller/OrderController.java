package stitch.crew.hour.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponses<OrderCreateResponse>> createOrder(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid OrderCreateRequest request
        ){
        OrderCreateResponse order = orderService.createOrder(
                currentUser.getId(),
                request
        );

        return ApiResult.created(
            SuccessCode.ORDER_CREATED,
            order
        );
    }

}
