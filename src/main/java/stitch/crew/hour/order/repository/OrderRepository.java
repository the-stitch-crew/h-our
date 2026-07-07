package stitch.crew.hour.order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.order.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    default Order findByIdOrThrow(Long orderId){
        return findById(orderId).orElseThrow(
                ()->new BusinessException(ErrorCode.ORDER_NOT_FOUND)
        );
    }

    Optional<Order> findOrderByOrderNumber(UUID orderNumber);

    default Order findByOrderNumberOrThrow(UUID orderNumber){
        return findOrderByOrderNumber(orderNumber).orElseThrow(
                ()->new BusinessException(ErrorCode.ORDER_NOT_FOUND)
        );
    }
}
