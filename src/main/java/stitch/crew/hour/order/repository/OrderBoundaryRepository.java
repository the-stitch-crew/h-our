package stitch.crew.hour.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.OrderSearchResponse;
import stitch.crew.hour.orderproduct.domain.OrderProduct;

import java.awt.*;
import java.util.UUID;

public interface OrderBoundaryRepository {
    Order saveOrder(Order order);

    OrderProduct saveOrderProduct(OrderProduct orderProduct);

    Order findOrderById(Long orderId);

    Order findByOrderNumberOrThrow(UUID orderNumber);

    Page<OrderSearchResponse> findOrderByUserId(Long userId, Pageable pageable);
}
