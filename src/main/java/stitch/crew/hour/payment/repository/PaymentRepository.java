package stitch.crew.hour.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.payment.domain.Payment;
import stitch.crew.hour.payment.dto.PaymentRequestBody;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentCustomRepository {
    default Payment findByIdOrThrow(Long paymentId){
        return findById(paymentId).orElseThrow(
                ()-> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND)
        );
    }

    @Query(
            """
            SELECT p 
                FROM Payment p
                WHERE p.orderNumber = :orderNumber
                AND p.paymentStatus = PaymentStatus.COMPLETED
            """
    )
    Optional<Payment> findPaymentByOrderNumber(UUID orderNumber);

}
