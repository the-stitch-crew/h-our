package stitch.crew.hour.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.payment.domain.Payment;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;

public interface PaymentCustomRepository {
    Page<PaymentDetailResponse> getPaymentDetails(
            Pageable pageable,
            Long userId
    );
}
