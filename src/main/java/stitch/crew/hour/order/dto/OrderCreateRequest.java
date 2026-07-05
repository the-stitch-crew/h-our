package stitch.crew.hour.order.dto;

import jakarta.validation.constraints.NotBlank;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;

import java.util.List;

public record OrderCreateRequest(
        List<OrderProductCreateRequest> requests,
        @NotBlank String address,
        @NotBlank String postalCode,
        String receiverName,
        @NotBlank String phoneNumber,
        String request,
        @NotBlank String ordererName,
        @NotBlank String receiverPhoneNumber
) {
}
