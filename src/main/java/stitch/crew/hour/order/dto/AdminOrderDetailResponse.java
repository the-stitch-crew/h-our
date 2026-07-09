package stitch.crew.hour.order.dto;

import stitch.crew.hour.order.domain.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminOrderDetailResponse(
        UUID orderNumber,
        String orderStatus,
        Integer totalPrice,
        Long deliveryFee,
        String ordererName,
        String phoneNumber,
        String receiverName,
        String receiverPhoneNumber,
        String address,
        String postalCode,
        String request,
        LocalDateTime createdAt,
        List<AdminOrderProductResponse> products
) {
    public static AdminOrderDetailResponse from(Order order) {
        return new AdminOrderDetailResponse(
                order.getOrderNumber(),
                order.getOrderStatus().name(),
                order.getTotalPrice(),
                order.getDeliveryFee(),
                order.getOrdererName(),
                order.getPhoneNumber(),
                order.getReceiverName(),
                order.getReceiverPhoneNumber(),
                order.getAddress(),
                order.getPostalCode(),
                order.getRequest(),
                order.getCreatedAt(),
                order.getOrderProduct().stream()
                        .map(AdminOrderProductResponse::from)
                        .toList()
        );
    }
}
