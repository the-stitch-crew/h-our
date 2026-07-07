package stitch.crew.hour.order.controller;

import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.*;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.user.domain.CurrentUser;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderSwaggerSupporter {

    private final OrderService orderService;

    @Override
    @PostMapping("/product")
    public ResponseEntity<ApiResponses<OrderCreateResponse>> createOrder(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid OrderCreateFromProductRequest request
    ){
        OrderCreateResponse order = orderService.createSingleOrder(
                currentUser.getId(),
                request
        );

        return ApiResult.created(
                SuccessCode.ORDER_CREATED_SUCCESS,
                order
        );
    }

    @Override
    @PostMapping("/cart")
    public ResponseEntity<ApiResponses<OrderCreateResponse>> createOrderFromCart(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid OrderCreateFromCartRequest request
        ){
        OrderCreateResponse order = orderService.createOrderFromCart(
                currentUser.getId(),
                request
        );

        return ApiResult.created(
            SuccessCode.ORDER_CREATED_SUCCESS,
            order
        );
    }

    @Override
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponses<OrderDetailResponse>> getOrderDetail(
           @AuthenticationPrincipal CurrentUser currentUser,
           @PathVariable UUID orderNumber
    ) {
        return ApiResult.ok(
            SuccessCode.ORDER_READ_SUCCESS,
                orderService.getOrderDetail(
                        currentUser.getId(),
                        orderNumber
                )
        );
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponses<Page<OrderSearchResponse>>> getOrderSearches(
            @AuthenticationPrincipal CurrentUser currentUser,
            Paging paging
    ) {
        return ApiResult.ok(
                SuccessCode.ORDER_READ_SUCCESS,
                orderService.getOrderSearches(
                        currentUser.getId(),
                        paging.toPageable()
                )
        );
    }


    @Override
    @PatchMapping("/{orderNumber}/purchased")
    public ResponseEntity<ApiResponses<Void>> setOrderPurchased(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable UUID orderNumber
    ) {
        orderService.setPaymentPurchased(
                currentUser.getId(),
                orderNumber
        );
        return ApiResult.ok(
                SuccessCode.ORDER_PURCHASED
        );
    }


    @Override
    @DeleteMapping("/{orderNumber}/canceled")
    public ResponseEntity<ApiResponses<Void>> setOrderCanceled(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable UUID orderNumber
    ) {
        orderService.setCanceled(
                currentUser.getId(),
                orderNumber
        );
        return ApiResult.ok(
                SuccessCode.ORDER_CANCELED
        );
    }
}
