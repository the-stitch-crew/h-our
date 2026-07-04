package stitch.crew.hour.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.product.ProductRepository;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.shippingpolicy.domain.ShippingPolicy;
import stitch.crew.hour.shippingpolicy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final UserRepository userRepository;
    private final OrderBoundaryRepository orderBoundaryRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderCreateResponse createOrder(
        Long userId,
        OrderCreateRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

        List<OrderProduct> orderProducts = request.requests().stream().map(
                (orderProductCreateRequest) -> orderBoundaryRepository.saveOrderProduct(
                            new OrderProduct(
                                    orderProductCreateRequest.productName(),
                                    orderProductCreateRequest.amount(),
                                    orderProductCreateRequest.price(),
                                    orderProductCreateRequest.productId(),
                                    orderProductCreateRequest.option()
                            )
                )
        ).toList();

        Order order = orderBoundaryRepository.saveOrder(
                new Order(
                        foundedUser,
                        orderProducts,
                        activeOrThrow.getDeliveryFee(),
                        request.address(),
                        request.postalCode(),
                        request.receiverName(),
                        request.phoneNumber(),
                        request.request(),
                        request.ordererName(),
                        request.receiverPhoneNumber()
                )
        );

        return OrderCreateResponse.from(order);
    }
}
