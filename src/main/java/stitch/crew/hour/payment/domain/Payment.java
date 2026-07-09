package stitch.crew.hour.payment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.payment.constant.PaymentMethod;
import stitch.crew.hour.payment.constant.PaymentStatus;
import stitch.crew.hour.payment.constant.PaymentType;
import stitch.crew.hour.payment.dto.PaymentRequestBody;
import stitch.crew.hour.reservation.domain.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID tossOrderId;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false, length = 255)
    private String tossPaymentKey;

    @Column(nullable = false)
    private UUID orderNumber;

    @Column(nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(length = 255)
    private String pgReceiptUrl;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private UUID idempotencyKey;

    @Column(nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Payment(
            Order order,
            Reservation reservation,
            PaymentRequestBody requestBody,
            PaymentMethod paymentMethod,
            PaymentType paymentType
    ){
        this.tossPaymentKey = requestBody.paymentKey();
        this.tossOrderId = UUID.fromString(requestBody.orderId());

        this.orderNumber = UUID.fromString(requestBody.orderNumber());
        this.idempotencyKey = UUID.randomUUID();
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod;
        this.requestedAt = LocalDateTime.now();
        this.paymentType = paymentType;

        if (paymentType.equals(PaymentType.ORDER)) {
            this.order = order;
            this.totalPrice = order.getTotalPrice().longValue();
        } else {
            this.reservation = reservation;
            this.totalPrice = requestBody.amount();
        }
    }

    public void switchPaymentStatus(PaymentStatus status){
        this.paymentStatus = status;
    }

    public void purchaseComplete(
            String pgReceiptUrl
    ){
        this.pgReceiptUrl = pgReceiptUrl;
        this.approvedAt = LocalDateTime.now();
    }
}
