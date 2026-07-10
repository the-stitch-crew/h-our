package stitch.crew.hour.payment.dto;

import stitch.crew.hour.payment.constant.PaymentType;


public record PaymentRequestBody(
        String paymentKey,
        String orderId,
        String orderNumber,
        String reservationNumber,
        PaymentType paymentType,
        Long amount
) {
    public PaymentRequestBody(
            String paymentKey,
            String orderId,
            String orderNumber,
            Long amount
    ) {
        this(paymentKey, orderId, orderNumber, null, PaymentType.ORDER, amount);
    }

    public PaymentType paymentTypeOrDefault() {
        return paymentType == null ? PaymentType.ORDER : paymentType;
    }
}
