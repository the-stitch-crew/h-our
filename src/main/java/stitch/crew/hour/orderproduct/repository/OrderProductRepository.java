package stitch.crew.hour.orderproduct.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
