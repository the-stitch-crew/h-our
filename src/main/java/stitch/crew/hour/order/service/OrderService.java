package stitch.crew.hour.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.repository.CartRepository;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.*;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Transactional(readOnly = true)
public class OrderService {
    private final UserRepository userRepository;
    private final OrderBoundaryRepository orderBoundaryRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Transactional
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    public OrderCreateResponse createSingleOrder(
        Long userId,
        OrderCreateFromProductRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

        Order savedOrder = orderBoundaryRepository.saveOrder(
                new Order(
                        foundedUser,
                        activeOrThrow.getDeliveryFee(),
                        request.address(),
                        request.postalCode(),
                        request.receiverName(),
                        request.request(),
                        request.receiverPhoneNumber()
                )
        );

        Product foundedProduct = productRepository.findByIdOrThrow(request.productId());

        OrderProduct savedOrderProduct = orderBoundaryRepository.saveOrderProduct(
                new OrderProduct(
                        foundedProduct.getName(),
                        request.amount(),
                        foundedProduct.getPrice(),
                        foundedProduct.getId(),
                        request.option(),
                        savedOrder
                )
        );

        savedOrder.setOrderProduct(savedOrderProduct);

        return OrderCreateResponse.from(savedOrder);
    }

    @Transactional
    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    public OrderCreateResponse createOrderFromCart(
            Long userId,
            OrderCreateFromCartRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        PreConditions.validate(
                foundedUser.getCart() != null,
                ErrorCode.NO_CART
        );

        ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

        Order order = orderBoundaryRepository.saveOrder(
                new Order(
                        foundedUser,
                        activeOrThrow.getDeliveryFee(),
                        request.address(),
                        request.postalCode(),
                        request.receiverName(),
                        request.request(),
                        request.receiverPhoneNumber()
                )
        );

        Cart cart = foundedUser.getCart();

        List<OrderProduct> orderProducts = cart.getCartProducts().stream().map(
                (cartProduct) -> orderBoundaryRepository.saveOrderProduct(
                        new OrderProduct(
                                cartProduct.getProductName(),
                                cartProduct.getAmount(),
                                cartProduct.getProductPrice(),
                                cartProduct.getProduct().getId(),
                                cartProduct.getOption(),
                                order
                        )
                )
        ).toList();

        order.setOrderProducts(orderProducts);

        return OrderCreateResponse.from(order);
    }

    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    public OrderDetailResponse getOrderDetail(
            Long userId,
            UUID orderNumber
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);
        Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);

        PreConditions.validate(
                validateAuthority(foundedUser,foundedOrder),
                ErrorCode.NO_AUTHORITY_ON_ORDER
        );

        return OrderDetailResponse.from(foundedOrder);
    }

    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    public OrderDetailResponse getOrderDetailFromPurchase(
            Long userId,
            UUID orderNumber
    ){

        User foundedUser = userRepository.findByIdOrthrow(userId);
        Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);

        PreConditions.validate(
                validateAuthority(foundedUser,foundedOrder),
                ErrorCode.NO_AUTHORITY_ON_ORDER
        );

        PreConditions.validate(
                foundedOrder.getOrderStatus().equals(OrderStatus.ORDERED),
                ErrorCode.PAYMENT_ALREADY_PAYED
        );

        return OrderDetailResponse.from(foundedOrder);
    }

    @PreAuthorize("isAuthenticated()")
    public Page<OrderSearchResponse> getOrderSearches(
            Long userId,
            Pageable pageable
    ){
        return orderBoundaryRepository.findOrderByUserId(
                userId,
                pageable
        );
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void setPaymentPurchased(
            Long userId,
            UUID orderNumber
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);
        Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);

        PreConditions.validate(
                validateAuthority(foundedUser,foundedOrder),
                ErrorCode.NO_AUTHORITY_ON_ORDER
        );

        foundedOrder.switchStatus(OrderStatus.PURCHASED);
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void setInDelivery(
            UUID orderNumber
    ){
        Order founded = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);
        founded.switchStatus(OrderStatus.IN_DELIVERY);
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void setDelivered(
            UUID orderNumber
    ){
        Order founded = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);
        founded.switchStatus(OrderStatus.DELIVERED);
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public void setComplete(
            UUID orderNumber
    ){
        Order founded = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);
        founded.switchStatus(OrderStatus.COMPLETE);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void setCanceled(
            Long userId,
            UUID orderNumber
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);
        Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderNumber);

        PreConditions.validate(
                validateAuthority(foundedUser,foundedOrder),
                ErrorCode.NO_AUTHORITY_ON_ORDER
        );

        foundedOrder.switchStatus(OrderStatus.CANCELED);
    }


    private Boolean validateAuthority(User user, Order order){
        if ( user.getRole().equals(Role.ADMIN) ) return true;

        if ( order.getOrderer().getId().equals(user.getId()) ) return true;

        return false;
    }
}
