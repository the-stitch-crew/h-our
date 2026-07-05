package stitch.crew.hour.order.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.repository.OrderProductRepository;

@Repository
@RequiredArgsConstructor
public class OrderBoundaryRepositoryImpl implements OrderBoundaryRepository {

    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;


    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }
}
