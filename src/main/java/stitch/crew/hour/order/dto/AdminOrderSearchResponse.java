package stitch.crew.hour.order.dto;

import stitch.crew.hour.order.domain.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminOrderSearchResponse(
        UUID orderNumber,
        String ordererName,
        String phoneNumber,
        Integer totalPrice,
        String orderStatus,
        LocalDateTime createdAt
) {
    public static AdminOrderSearchResponse from(Order order) {
        return new AdminOrderSearchResponse(
                order.getOrderNumber(),
                order.getOrdererName(),
                order.getPhoneNumber(),
                order.getTotalPrice(),
                order.getOrderStatus().name(),
                order.getCreatedAt()
        );
    }
}
