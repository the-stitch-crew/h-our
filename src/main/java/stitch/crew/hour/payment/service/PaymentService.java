package stitch.crew.hour.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.repository.OrderRepository;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.payment.constant.PaymentMethod;
import stitch.crew.hour.payment.constant.PaymentStatus;
import stitch.crew.hour.payment.constant.PaymentType;
import stitch.crew.hour.payment.domain.Payment;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;
import stitch.crew.hour.payment.dto.PaymentRequestBody;
import stitch.crew.hour.payment.repository.PaymentRepository;
import stitch.crew.hour.reservation.domain.Reservation;
import stitch.crew.hour.reservation.domain.ReservationStatus;
import stitch.crew.hour.reservation.service.ReservationService;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ReservationService reservationService;

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public Payment initPayment(
            PaymentRequestBody request
    ){
        if (request.paymentTypeOrDefault().equals(PaymentType.RESERVATION)) {
            return initPaymentByReservation(request);
        }

        return initPaymentByOrder(request);
    }

    public Payment initPaymentByOrder(
            PaymentRequestBody request
    ) {
        Order foundedOrder = orderRepository.findByOrderNumberOrThrow(
                UUID.fromString(request.orderNumber())
        );


        PreConditions.validate(
                foundedOrder.getOrderStatus().equals(OrderStatus.ORDERED),
                ErrorCode.PAYMENT_ALREADY_PAYED
        );


        Payment payment = paymentRepository.save(
                new Payment(
                        foundedOrder,
                        null,
                        request,
                        PaymentMethod.EASY_PAY,
                        PaymentType.ORDER
                )
        );

        log.info("order payment initialized",
                kv("event", "payment_initialized"),
                kv("paymentId", payment.getId()),
                kv("paymentType", payment.getPaymentType()),
                kv("orderNumber", payment.getOrderNumber()),
                kv("tossOrderId", payment.getTossOrderId()),
                kv("amount", payment.getTotalPrice()),
                kv("paymentStatus", payment.getPaymentStatus())
        );

        return payment;
    }

    private Payment initPaymentByReservation(
            PaymentRequestBody request
    ) {
        Reservation foundedReservation = reservationService.getReservationWithNumber(
                UUID.fromString(request.reservationNumber())
        );

        PreConditions.validate(
                foundedReservation.getStatus().equals(ReservationStatus.PENDING),
                ErrorCode.RESERVATION_NOT_PENDING
        );

        Payment payment = paymentRepository.save(
                new Payment(
                        null,
                        foundedReservation,
                        request,
                        PaymentMethod.EASY_PAY,
                        PaymentType.RESERVATION
                )
        );

        log.info("reservation payment initialized",
                kv("event", "payment_initialized"),
                kv("paymentId", payment.getId()),
                kv("paymentType", payment.getPaymentType()),
                kv("reservationNumber", payment.getReservationNumber()),
                kv("tossOrderId", payment.getTossOrderId()),
                kv("amount", payment.getTotalPrice()),
                kv("paymentStatus", payment.getPaymentStatus())
        );

        return payment;
    }

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public void confirmPayment(
        Long userId,
        Payment payment,
        String pgReceiptUrl
    ){
        PaymentStatus beforeStatus = payment.getPaymentStatus();

        if (payment.getPaymentType().equals(PaymentType.RESERVATION)) {
            Reservation foundedReservation = payment.getReservation();

            PreConditions.validate(
                    validateCondition(foundedReservation, userId),
                    ErrorCode.PAYMENT_PERMISSION_DENY
            );

            reservationService.confirmReservation(foundedReservation);
        } else {
            Order foundedOrder = payment.getOrder();

            orderService.setPaymentPurchased(
                    userId,
                    foundedOrder.getOrderNumber()
            );
        }

        payment.switchPaymentStatus(PaymentStatus.COMPLETED);
        payment.purchaseComplete(pgReceiptUrl);

        log.info("payment status changed",
                kv("event", "payment_status_changed"),
                kv("userId", userId),
                kv("paymentId", payment.getId()),
                kv("paymentType", payment.getPaymentType()),
                kv("orderNumber", payment.getOrderNumber()),
                kv("reservationNumber", payment.getReservationNumber()),
                kv("beforeStatus", beforeStatus),
                kv("afterStatus", payment.getPaymentStatus()),
                kv("amount", payment.getTotalPrice()),
                kv("pgReceiptIssued", pgReceiptUrl != null)
        );
    }

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public void cancelPayment(
        Payment payment
    ){
        PaymentStatus beforeStatus = payment.getPaymentStatus();
        payment.switchPaymentStatus(PaymentStatus.CANCELED);

        log.warn("payment status changed",
                kv("event", "payment_status_changed"),
                kv("paymentId", payment.getId()),
                kv("paymentType", payment.getPaymentType()),
                kv("orderNumber", payment.getOrderNumber()),
                kv("reservationNumber", payment.getReservationNumber()),
                kv("beforeStatus", beforeStatus),
                kv("afterStatus", payment.getPaymentStatus()),
                kv("amount", payment.getTotalPrice())
        );
    }

    @PreAuthorize(value = "isAuthenticated()")
    public Payment getPayment(
            Long paymentId
    ){
        return paymentRepository.findByIdOrThrow(paymentId);
    }


    @PreAuthorize(value = "isAuthenticated()")
    public PaymentDetailResponse getPaymentDetail(
        Long userId,
        Long paymentId
    ){
        Payment foundedPayment = paymentRepository.findByIdOrThrow(paymentId);

        PreConditions.validate(
                validateCondition(foundedPayment, userId),
                ErrorCode.PAYMENT_PERMISSION_DENY
        );

        return PaymentDetailResponse.from(foundedPayment);
    }

    @PreAuthorize(value = "isAuthenticated()")
    public PaymentDetailResponse getPurchasedPaymentDetailByOrderNumber(
            Long userId,
            UUID orderNumber
    ){
        Optional<Payment> foundedPayment = paymentRepository.findPaymentByOrderNumber(orderNumber);

        PreConditions.validate(
                foundedPayment.isPresent(),
                ErrorCode.PAYMENT_NOT_PAYED
        );

        Order order = foundedPayment.get().getOrder();

        PreConditions.validate(
                validateCondition(order, userId),
                ErrorCode.PAYMENT_PERMISSION_DENY
        );

        return PaymentDetailResponse.from(foundedPayment.get());
    }


    @PreAuthorize(value = "isAuthenticated()")
    public Page<PaymentDetailResponse> getPaymentSearch(
            Long userId,
            Pageable pageable
    ){
        return paymentRepository.getPaymentDetails(pageable, userId);
    }


    @PreAuthorize(value = "isAuthenticated()")
    public PaymentDetailResponse getReceipt(
            Long userId,
            Long paymentId
    ){
        Payment foundedPayment = paymentRepository.findByIdOrThrow(paymentId);

        PreConditions.validate(
                validateCondition(foundedPayment, userId),
                ErrorCode.PAYMENT_PERMISSION_DENY
        );

        PreConditions.validate(
                !foundedPayment.getPaymentStatus().equals(PaymentStatus.PENDING),
                ErrorCode.NO_ACCESS_ON_RECEIPT
        );

        return PaymentDetailResponse.from(foundedPayment);
    }

    @PreAuthorize(value = "isAuthenticated()")
    @Transactional
    public void refundPaymentByPaymentId(
            Long userId,
            Long paymentId
    ){
        Payment foundedPayment = paymentRepository.findByIdOrThrow(paymentId);

        PreConditions.validate(
                foundedPayment.getPaymentStatus().equals(PaymentStatus.COMPLETED),
                ErrorCode.PAYMENT_NOT_PAYED
        );

        PreConditions.validate(
                validateCondition(foundedPayment, userId),
                ErrorCode.PAYMENT_PERMISSION_DENY
        );

        PaymentStatus beforeStatus = foundedPayment.getPaymentStatus();

        if (foundedPayment.getPaymentType().equals(PaymentType.RESERVATION)) {
            reservationService.cancelReservation(foundedPayment.getReservation());
        }

        foundedPayment.switchPaymentStatus(PaymentStatus.REFUNDED);

        log.info("payment refunded",
                kv("event", "payment_refunded"),
                kv("userId", userId),
                kv("paymentId", foundedPayment.getId()),
                kv("paymentType", foundedPayment.getPaymentType()),
                kv("orderNumber", foundedPayment.getOrderNumber()),
                kv("reservationNumber", foundedPayment.getReservationNumber()),
                kv("beforeStatus", beforeStatus),
                kv("afterStatus", foundedPayment.getPaymentStatus()),
                kv("amount", foundedPayment.getTotalPrice())
        );
    }

    private Boolean validateCondition(
            Payment payment,
            Long userId
    ) {
        if (payment.getPaymentType().equals(PaymentType.RESERVATION)) {
            return validateCondition(payment.getReservation(), userId);
        }

        return validateCondition(payment.getOrder(), userId);
    }

    private Boolean validateCondition(
            Order order,
            Long userId
    ){
        if ( order.getOrderer().getId().equals(userId) ) return true;

        User foundedUser = userRepository.findByIdOrthrow(userId);

        if ( foundedUser.getRole().equals(Role.ADMIN) ) return true;

        return false;
    }

    private Boolean validateCondition(
            Reservation reservation,
            Long userId
    ){
        if ( reservation.getUser().getId().equals(userId) ) return true;

        User foundedUser = userRepository.findByIdOrthrow(userId);

        if ( foundedUser.getRole().equals(Role.ADMIN) ) return true;

        return false;
    }

}
