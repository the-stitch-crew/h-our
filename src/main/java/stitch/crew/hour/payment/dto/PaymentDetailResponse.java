package stitch.crew.hour.payment.dto;

import com.querydsl.core.annotations.QueryProjection;
import stitch.crew.hour.payment.constant.PaymentStatus;
import stitch.crew.hour.payment.domain.Payment;

import java.util.UUID;

public record PaymentDetailResponse(
        Long paymentId,
        UUID orderNumber,
        String paymentStatus,
        String paymentMethod,
        String pgReceiptUrl,
        String requestedAt,
        String approvedAt
) {

    @QueryProjection
    public PaymentDetailResponse{
    }

    public static PaymentDetailResponse from(Payment payment){
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getOrderNumber(),
                payment.getPaymentStatus().name(),
                payment.getPaymentMethod().name(),
                payment.getPgReceiptUrl(),
                payment.getRequestedAt().toString(),
                payment.getApprovedAt() == null ? null : payment.getApprovedAt().toString()
        );
    }
}
