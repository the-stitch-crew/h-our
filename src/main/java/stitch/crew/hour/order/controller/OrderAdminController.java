package stitch.crew.hour.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.dto.AdminOrderDetailResponse;
import stitch.crew.hour.order.dto.AdminOrderSearchResponse;
import stitch.crew.hour.order.service.OrderAdminService;
import stitch.crew.hour.order.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController implements OrderAdminSwaggerSupporter {

    private final OrderService orderService;
    private final OrderAdminService orderAdminService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponses<Page<AdminOrderSearchResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status
    ) {
        Page<AdminOrderSearchResponse> response = orderAdminService.getOrders(page, size, status);
        return ApiResult.ok(SuccessCode.ORDER_READ_SUCCESS, response);
    }

    @Override
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponses<AdminOrderDetailResponse>> getOrder(
            @PathVariable UUID orderNumber
    ) {
        AdminOrderDetailResponse response = orderAdminService.getOrder(orderNumber);
        return ApiResult.ok(SuccessCode.ORDER_READ_SUCCESS, response);
    }

    @Override
    @PatchMapping("/{orderNumber}/indelivery")
    public ResponseEntity<ApiResponses<Void>> setOrderInDelivery(
            @PathVariable UUID orderNumber
    ) {
        orderService.setInDelivery(orderNumber);
        return ApiResult.ok(
                SuccessCode.ORDER_IN_DELIVERY
        );
    }

    @Override
    @PatchMapping("/{orderNumber}/delivered")
    public ResponseEntity<ApiResponses<Void>> setOrderDelivered(
            @PathVariable UUID orderNumber
    ) {
        orderService.setDelivered(orderNumber);
        return ApiResult.ok(
                SuccessCode.ORDER_DELIVERED
        );
    }

    @Override
    @PatchMapping("/{orderNumber}/complete")
    public ResponseEntity<ApiResponses<Void>> setOrderComplete(
           @PathVariable UUID orderNumber
    ) {
        orderService.setComplete(orderNumber);
        return ApiResult.ok(
                SuccessCode.ORDER_COMPLETE
        );
    }

    @Override
    @PatchMapping("/{orderNumber}/cancel")
    public ResponseEntity<ApiResponses<Void>> cancelOrder(
            @PathVariable UUID orderNumber
    ) {
        orderAdminService.cancelOrder(orderNumber);
        return ApiResult.ok(
                SuccessCode.ORDER_CANCELED
        );
    }
}
