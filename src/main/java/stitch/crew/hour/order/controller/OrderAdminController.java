package stitch.crew.hour.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController implements OrderAdminSwaggerSupporter {

    private final OrderService orderService;

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
}
