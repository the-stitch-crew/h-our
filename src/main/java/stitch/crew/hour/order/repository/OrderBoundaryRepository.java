package stitch.crew.hour.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.orderproduct.domain.OrderProduct;

import java.awt.*;

public interface OrderBoundaryRepository {
    Order saveOrder(Order order);

    OrderProduct saveOrderProduct(OrderProduct orderProduct);
}
