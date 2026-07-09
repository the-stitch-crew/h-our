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
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public Payment initPaymentByOrder(
            PaymentRequestBody request
    ){
        Order foundedOrder = orderRepository.findByOrderNumberOrThrow(
                UUID.fromString(request.orderNumber())
        );


        PreConditions.validate(
                foundedOrder.getOrderStatus().equals(OrderStatus.ORDERED),
                ErrorCode.PAYMENT_ALREADY_PAYED
        );


        return paymentRepository.save(
                new Payment(
                        foundedOrder,
                        null,
                        request,
                        PaymentMethod.EASY_PAY,
                        PaymentType.ORDER
                )
        );
    }

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public void confirmPayment(
        Long userId,
        Payment payment,
        String pgReceiptUrl
    ){
        Order foundedOrder = payment.getOrder();

        orderService.setPaymentPurchased(
                userId,
                foundedOrder.getOrderNumber()
        );

        payment.switchPaymentStatus(PaymentStatus.COMPLETED);
        payment.purchaseComplete(pgReceiptUrl);
    }

    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public void cancelPayment(
        Payment payment
    ){
        payment.switchPaymentStatus(PaymentStatus.CANCELED);
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

        Order order = foundedPayment.getOrder();

        PreConditions.validate(
                validateCondition(order, userId),
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

        Order order = foundedPayment.getOrder();

        PreConditions.validate(
                validateCondition(order, userId),
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

        Order order = foundedPayment.getOrder();

        PreConditions.validate(
                validateCondition(order, userId),
                ErrorCode.PAYMENT_PERMISSION_DENY
        );

        foundedPayment.switchPaymentStatus(PaymentStatus.REFUNDED);
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

}
