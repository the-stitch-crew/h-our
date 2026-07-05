package stitch.crew.hour.order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
