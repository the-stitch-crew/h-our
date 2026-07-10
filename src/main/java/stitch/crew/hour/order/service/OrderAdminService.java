package stitch.crew.hour.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.AdminOrderDetailResponse;
import stitch.crew.hour.order.dto.AdminOrderSearchResponse;
import stitch.crew.hour.order.repository.OrderRepository;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderAdminService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final OrderRepository orderRepository;

    public Page<AdminOrderSearchResponse> getOrders(
            int page,
            int size,
            OrderStatus status
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, DEFAULT_SORT);
        Page<Order> orders = status == null
                ? orderRepository.findAll(pageRequest)
                : orderRepository.findAllByOrderStatus(status, pageRequest);

        return orders.map(AdminOrderSearchResponse::from);
    }

    public AdminOrderDetailResponse getOrder(UUID orderNumber) {
        Order order = orderRepository.findByOrderNumberOrThrow(orderNumber);
        return AdminOrderDetailResponse.from(order);
    }

    @Transactional
    public void cancelOrder(UUID orderNumber) {
        Order order = orderRepository.findByOrderNumberOrThrow(orderNumber);
        OrderStatus beforeStatus = order.getOrderStatus();
        order.switchStatus(OrderStatus.CANCELED);

        log.info("order status changed",
                kv("event", "order_status_changed"),
                kv("orderNumber", order.getOrderNumber()),
                kv("beforeStatus", beforeStatus),
                kv("afterStatus", order.getOrderStatus()),
                kv("totalPrice", order.getTotalPrice())
        );
    }
}
