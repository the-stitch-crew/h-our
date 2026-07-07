package stitch.crew.hour.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.domain.QOrder;
import stitch.crew.hour.order.dto.OrderSearchResponse;
import stitch.crew.hour.order.dto.QOrderSearchResponse;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.repository.OrderProductRepository;
import stitch.crew.hour.product.dto.QProductSearchResponse;
import stitch.crew.hour.user.domain.QUser;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderBoundaryRepositoryImpl implements OrderBoundaryRepository {

    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final JPAQueryFactory jpaQueryFactory;

    private final QOrder qOrder = QOrder.order;
    private final QUser qUser = QUser.user;


    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findByIdOrThrow(orderId);
    }

    @Override
    public Order findByOrderNumberOrThrow(UUID orderNumber) {
        return orderRepository.findByOrderNumberOrThrow(orderNumber);
    }

    @Override
    public Page<OrderSearchResponse> findOrderByUserId(Long userId, Pageable pageable) {

        List<OrderSearchResponse> fetch = jpaQueryFactory.select(
                        new QOrderSearchResponse(
                                qOrder.orderNumber,
                                qOrder.totalPrice,
                                qOrder.orderStatus.stringValue()
                        )
                ).from(qOrder)
                .join(qUser)
                .on(qOrder.orderer.id.eq(qUser.id))
                .fetchJoin()
                .where(qUser.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int size = jpaQueryFactory.selectFrom(qOrder)
                .join(qUser)
                .on(qOrder.orderer.id.eq(qUser.id))
                .fetchJoin()
                .where(qUser.id.eq(userId))
                .fetch().size();

        return new PageImpl(
                fetch,
                pageable,
                size
        );
    }
}
