package stitch.crew.hour.payment.dto;


public record PaymentRequestBody(
        String paymentKey,
        String orderId,
        String orderNumber,
        Long amount
) {
}
