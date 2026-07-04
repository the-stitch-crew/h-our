package stitch.crew.hour.order.dto;

import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;

import java.util.List;

public record OrderCreateRequest(
        List<OrderProductCreateRequest> requests,
        String address,
        String postalCode,
        String receiverName,
        String phoneNumber,
        String request,
        String ordererName,
        String receiverPhoneNumber
) {
}
