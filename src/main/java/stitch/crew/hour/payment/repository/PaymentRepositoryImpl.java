package stitch.crew.hour.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.order.domain.QOrder;
import stitch.crew.hour.payment.domain.QPayment;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;
import stitch.crew.hour.payment.dto.QPaymentDetailResponse;
import stitch.crew.hour.reservation.domain.QReservation;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final QPayment qPayment = QPayment.payment;
    private final QOrder qOrder = QOrder.order;
    private final QReservation qReservation = QReservation.reservation;

    @Override
    public Page<PaymentDetailResponse> getPaymentDetails(
            Pageable pageable,
            Long userId
    ) {
        List<PaymentDetailResponse> fetched = jpaQueryFactory.select(
                        new QPaymentDetailResponse(
                                qPayment.id,
                                qPayment.orderNumber,
                                qPayment.paymentStatus.stringValue(),
                                qPayment.paymentMethod.stringValue(),
                                qPayment.pgReceiptUrl,
                                qPayment.requestedAt.stringValue(),
                                qPayment.approvedAt.stringValue()
                        )
                ).from(qPayment)
                .leftJoin(qOrder)
                .on(qPayment.order.id.eq(qOrder.id))
                .leftJoin(qReservation)
                .on(qPayment.reservation.id.eq(qReservation.id))
                .where(qOrder.orderer.id.eq(userId).or(qReservation.user.id.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long size = jpaQueryFactory.select(
                        qPayment.count()
                ).from(qPayment)
                .leftJoin(qOrder)
                .on(qPayment.order.id.eq(qOrder.id))
                .leftJoin(qReservation)
                .on(qPayment.reservation.id.eq(qReservation.id))
                .where(qOrder.orderer.id.eq(userId).or(qReservation.user.id.eq(userId)))
                .fetchOne();
        return new PageImpl<>(
                fetched,
                pageable,
                size
        );
    }
}
