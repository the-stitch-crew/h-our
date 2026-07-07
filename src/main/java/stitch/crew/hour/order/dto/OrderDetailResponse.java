package stitch.crew.hour.order.dto;

import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.orderproduct.domain.QOrderProduct;
import stitch.crew.hour.orderproduct.dto.OrderProductDetailResponse;

import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
    UUID orderNumber,
    Integer totalPrice,
    String orderStatus,
    Long deliveryFee,
    String address,
    String postalCode,
    String receiverName,
    String receiverPhoneNumber,
    List<OrderProductDetailResponse> orderProducts
) {
    public static OrderDetailResponse from(Order order){
        return new OrderDetailResponse(
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getOrderStatus().name(),
                order.getDeliveryFee(),
                order.getAddress(),
                order.getPostalCode(),
                order.getReceiverName(),
                order.getReceiverPhoneNumber(),
                order.getOrderProduct().stream().map((s)->
                        OrderProductDetailResponse.from(s)).toList()
        );
    }
}
