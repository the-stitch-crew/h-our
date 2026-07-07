package stitch.crew.hour.order.dto;

import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.orderproduct.dto.OrderProductDetailResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderCreateResponse(
    UUID orderNumber,
    List<OrderProductDetailResponse> orderProducts,
    Integer totalPrice,
    Long deliveryFee,
    String address,
    String postalCode,
    String receiverName,
    String receiverPhoneNumber,
    String phoneNumber,
    String ordererName,
    String orderStatus,
    LocalDateTime createdAt
) {
    public static OrderCreateResponse from(Order order){
        return new OrderCreateResponse(
            order.getOrderNumber(),
            order.getOrderProduct().stream().map(
                    orderProduct -> OrderProductDetailResponse
                            .from(orderProduct)
            ).toList(),
            order.getTotalPrice(),
            order.getDeliveryFee(),
            order.getAddress(),
            order.getPostalCode(),
            order.getReceiverName(),
            order.getReceiverPhoneNumber(),
            order.getPhoneNumber(),
            order.getOrdererName(),
            order.getOrderStatus().name(),
            order.getCreatedAt()
        );
    }
}
