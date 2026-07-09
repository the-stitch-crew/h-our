package stitch.crew.hour.order.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.AdminOrderDetailResponse;
import stitch.crew.hour.order.dto.AdminOrderSearchResponse;
import stitch.crew.hour.order.repository.OrderRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.repository.OrderProductRepository;
import stitch.crew.hour.policy.domain.ShippingPolicy;
import stitch.crew.hour.policy.repository.ShippingPolicyRepository;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("OrderAdminService의")
class OrderAdminServiceTest {

    @Autowired
    OrderAdminService orderAdminService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ShippingPolicyRepository shippingPolicyRepository;

    @Autowired
    OrderProductRepository orderProductRepository;

    @Autowired
    EntityManager entityManager;

    User testUser;
    Product testProduct;
    int sequence;

    @BeforeEach
    void setUp() {
        testUser = saveUser();

        Category testCategory = categoryRepository.save(
                new Category("관리자 주문")
        );
        shippingPolicyRepository.save(
                new ShippingPolicy(
                        4000L,
                        3000L,
                        true
                )
        );
        testProduct = productRepository.save(
                new Product(
                        "테스트 상품",
                        2_000L,
                        "요약",
                        "설명",
                        testCategory
                )
        );
    }

    @Nested
    @DisplayName("Describe : getOrders()는")
    class Describe_getOrders {

        @Nested
        @DisplayName("Context : status 파라미터가 없는 경우")
        class Context_without_status {

            @Test
            @DisplayName("It : 전체 주문을 최신순으로 조회")
            void It_전체_주문을_최신순으로_조회() {
                // given
                Order firstOrder = saveOrder(OrderStatus.ORDERED, todayAt(9));
                Order thirdOrder = saveOrder(OrderStatus.PURCHASED, todayAt(10));
                Order secondOrder = saveOrder(OrderStatus.CANCELED, todayAt(11));

                // when
                Page<AdminOrderSearchResponse> response = orderAdminService.getOrders(
                        0,
                        20,
                        null
                );

                // then
                Assertions.assertThat(response.getTotalElements()).isEqualTo(3L);
                Assertions.assertThat(response.getContent())
                        .extracting(AdminOrderSearchResponse::orderNumber)
                        .containsSubsequence(
                                secondOrder.getOrderNumber(),
                                thirdOrder.getOrderNumber(),
                                firstOrder.getOrderNumber()
                        );
            }
        }

        @Nested
        @DisplayName("Context : PURCHASED 상태가 주어진 경우")
        class Context_with_purchased_status {

            @Test
            @DisplayName("It : 결제 완료 주문만 조회")
            void It_결제완료_주문만_조회() {
                // given
                saveOrder(OrderStatus.ORDERED, todayAt(9));
                saveOrder(OrderStatus.PURCHASED, todayAt(10));
                saveOrder(OrderStatus.CANCELED, todayAt(11));

                // when
                Page<AdminOrderSearchResponse> response = orderAdminService.getOrders(
                        0,
                        20,
                        OrderStatus.PURCHASED
                );

                // then
                Assertions.assertThat(response.getTotalElements()).isEqualTo(1L);
                Assertions.assertThat(response.getContent())
                        .extracting(AdminOrderSearchResponse::orderStatus)
                        .containsExactly(OrderStatus.PURCHASED.name());
            }
        }
    }

    @Nested
    @DisplayName("Describe : getOrder()는")
    class Describe_getOrder {

        @Nested
        @DisplayName("Context : 존재하는 주문번호가 주어진 경우")
        class Context_with_existing_order_number {

            @Test
            @DisplayName("It : 주문자, 배송지, 상품 목록을 포함한 주문 상세를 조회")
            void It_주문_상세를_조회() {
                // given
                Order order = saveOrder(OrderStatus.PURCHASED, todayAt(10));

                // when
                AdminOrderDetailResponse response = orderAdminService.getOrder(
                        order.getOrderNumber()
                );

                // then
                Assertions.assertThat(response.orderNumber()).isEqualTo(order.getOrderNumber());
                Assertions.assertThat(response.ordererName()).isEqualTo(testUser.getUserName());
                Assertions.assertThat(response.phoneNumber()).isEqualTo(testUser.getPhoneNumber());
                Assertions.assertThat(response.receiverName()).isEqualTo("수령자");
                Assertions.assertThat(response.receiverPhoneNumber()).isEqualTo("01099998888");
                Assertions.assertThat(response.address()).isEqualTo("서울시 테스트구");
                Assertions.assertThat(response.postalCode()).isEqualTo("12345");
                Assertions.assertThat(response.products()).hasSize(1);
                Assertions.assertThat(response.products().getFirst().productId()).isEqualTo(testProduct.getId());
                Assertions.assertThat(response.products().getFirst().option()).isEqualTo("옵션");
            }
        }

        @Nested
        @DisplayName("Context : 존재하지 않는 주문번호가 주어진 경우")
        class Context_with_not_existing_order_number {

            @Test
            @DisplayName("It : ORDER_NOT_FOUND 예외가 발생")
            void It_ORDER_NOT_FOUND_예외_발생() {
                // when
                BusinessException exception = assertThrows(
                        BusinessException.class,
                        () -> orderAdminService.getOrder(UUID.randomUUID())
                );

                // then
                Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("Describe : cancelOrder()는")
    class Describe_cancelOrder {

        @Nested
        @DisplayName("Context : 존재하는 주문번호가 주어진 경우")
        class Context_with_existing_order_number {

            @Test
            @DisplayName("It : 주문 상태를 취소로 전환")
            void It_주문상태를_취소로_전환() {
                // given
                Order order = saveOrder(OrderStatus.PURCHASED, todayAt(10));

                // when
                orderAdminService.cancelOrder(order.getOrderNumber());
                entityManager.flush();
                entityManager.clear();

                // then
                Order foundedOrder = orderRepository.findByOrderNumberOrThrow(order.getOrderNumber());
                Assertions.assertThat(foundedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
            }
        }
    }

    private User saveUser() {
        int id = ++sequence;
        return userRepository.save(
                new User(
                        "주문자" + id,
                        "admin-order" + id + "@test.com",
                        "1234",
                        LocalDate.of(1990, 1, 1),
                        Role.USER,
                        Gender.MALE,
                        null,
                        "0101234" + String.format("%04d", id),
                        "KR",
                        false,
                        false
                )
        );
    }

    private Order saveOrder(OrderStatus status, LocalDateTime createdAt) {
        Order order = orderRepository.save(
                new Order(
                        testUser,
                        3_500L,
                        "서울시 테스트구",
                        "12345",
                        "수령자",
                        "요청사항",
                        "01099998888"
                )
        );

        OrderProduct orderProduct = orderProductRepository.save(
                new OrderProduct(
                        testProduct.getName(),
                        2L,
                        testProduct.getPrice(),
                        testProduct.getId(),
                        "옵션",
                        order
                )
        );

        order.setOrderProduct(orderProduct);
        order.switchStatus(status);
        setOrderCreatedAt(order, createdAt);

        return order;
    }

    private void setOrderCreatedAt(Order order, LocalDateTime createdAt) {
        entityManager.flush();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Order> update = criteriaBuilder.createCriteriaUpdate(Order.class);
        Root<Order> root = update.from(Order.class);
        Path<LocalDateTime> createdAtPath = root.get("createdAt");
        Path<Long> idPath = root.get("id");

        update.set(createdAtPath, createdAt);
        update.where(criteriaBuilder.equal(idPath, order.getId()));

        entityManager.createQuery(update).executeUpdate();
        entityManager.clear();
    }

    private LocalDateTime todayAt(int hour) {
        return LocalDate.now().atTime(hour, 0);
    }
}
