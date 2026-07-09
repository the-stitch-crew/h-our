package stitch.crew.hour.admin.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.admin.dto.AdminDashboardResponse;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.repository.OrderRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.repository.OrderProductRepository;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("AdminDashboardService는")
class AdminDashboardServiceTest {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Category category;
    private int sequence;

    @BeforeEach
    void setUp() {
        user = saveUser("user");
        category = categoryRepository.save(new Category("대시보드"));
    }

    @Test
    @DisplayName("PURCHASED 주문은 매출에 포함한다")
    void it_includes_purchased_order_sales() {
        saveOrder(OrderStatus.PURCHASED, 10_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isEqualTo(10_000L);
    }

    @Test
    @DisplayName("IN_DELIVERY 주문은 매출에 포함한다")
    void it_includes_in_delivery_order_sales() {
        saveOrder(OrderStatus.IN_DELIVERY, 20_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isEqualTo(20_000L);
    }

    @Test
    @DisplayName("DELIVERED 주문은 매출에 포함한다")
    void it_includes_delivered_order_sales() {
        saveOrder(OrderStatus.DELIVERED, 30_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isEqualTo(30_000L);
    }

    @Test
    @DisplayName("COMPLETE 주문은 매출에 포함한다")
    void it_includes_complete_order_sales() {
        saveOrder(OrderStatus.COMPLETE, 40_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isEqualTo(40_000L);
    }

    @Test
    @DisplayName("ORDERED 주문은 매출에서 제외한다")
    void it_excludes_ordered_order_sales() {
        saveOrder(OrderStatus.ORDERED, 50_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isZero();
    }

    @Test
    @DisplayName("CANCELED 주문은 매출에서 제외한다")
    void it_excludes_canceled_order_sales() {
        saveOrder(OrderStatus.CANCELED, 60_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalSales()).isZero();
    }

    @Test
    @DisplayName("오늘 매출과 전체 매출을 분리해서 계산한다")
    void it_calculates_today_sales_and_total_sales_separately() {
        saveOrder(OrderStatus.PURCHASED, 10_000L, 1L, todayAt(10));
        saveOrder(OrderStatus.PURCHASED, 20_000L, 1L, yesterdayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.todaySales()).isEqualTo(10_000L);
        assertThat(response.totalSales()).isEqualTo(30_000L);
    }

    @Test
    @DisplayName("오늘 주문 수는 createdAt 기준으로 계산한다")
    void it_counts_today_orders_by_created_at() {
        saveOrder(OrderStatus.ORDERED, 10_000L, 1L, todayAt(9));
        saveOrder(OrderStatus.CANCELED, 20_000L, 1L, todayAt(11));
        saveOrder(OrderStatus.PURCHASED, 30_000L, 1L, yesterdayAt(11));

        AdminDashboardResponse response = dashboard();

        assertThat(response.todayOrderCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("삭제된 회원은 회원 수에서 제외한다")
    void it_excludes_deleted_users() {
        Long baseline = dashboard().totalUserCount();
        saveUser("active");
        User deletedUser = saveUser("deleted");
        setUserDeletedAt(deletedUser, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.totalUserCount()).isEqualTo(baseline + 1L);
    }

    @Test
    @DisplayName("오늘 가입자 수는 createdAt 기준으로 계산한다")
    void it_counts_today_users_by_created_at() {
        Long baseline = dashboard().todayUserCount();
        User todayUser = saveUser("today");
        User yesterdayUser = saveUser("yesterday");
        User deletedTodayUser = saveUser("deleted-today");
        setUserCreatedAt(todayUser, todayAt(8));
        setUserCreatedAt(yesterdayUser, yesterdayAt(8));
        setUserCreatedAt(deletedTodayUser, todayAt(9));
        setUserDeletedAt(deletedTodayUser, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.todayUserCount()).isEqualTo(baseline + 1L);
    }

    @Test
    @DisplayName("ACTIVATED 상품 수를 계산한다")
    void it_counts_activated_products() {
        saveProduct("활성1", ProductStatus.ACTIVATED);
        saveProduct("활성2", ProductStatus.ACTIVATED);
        saveProduct("품절", ProductStatus.SOLD_OUT);

        AdminDashboardResponse response = dashboard();

        assertThat(response.activeProductCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("SOLD_OUT 상품 수를 계산한다")
    void it_counts_sold_out_products() {
        saveProduct("활성", ProductStatus.ACTIVATED);
        saveProduct("품절1", ProductStatus.SOLD_OUT);
        saveProduct("품절2", ProductStatus.SOLD_OUT);

        AdminDashboardResponse response = dashboard();

        assertThat(response.soldOutProductCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("최근 주문은 최신순 5개만 반환한다")
    void it_returns_recent_five_orders_by_created_at_desc() {
        for (int i = 0; i < 6; i++) {
            saveOrder(OrderStatus.ORDERED, 10_000L + i, 1L, todayAt(i));
        }

        AdminDashboardResponse response = dashboard();

        assertThat(response.recentOrders()).hasSize(5);
        assertThat(response.recentOrders())
                .isSortedAccordingTo(Comparator.comparing(recentOrder -> recentOrder.createdAt(), Comparator.reverseOrder()));
        assertThat(response.recentOrders().get(0).totalPrice()).isEqualTo(10_005);
        assertThat(response.recentOrders()).noneMatch(recentOrder -> recentOrder.totalPrice().equals(10_000));
    }

    @Test
    @DisplayName("인기 상품은 totalSales desc, totalQuantity desc 순으로 5개 반환한다")
    void it_returns_top_five_products_ordered_by_sales_and_quantity() {
        Product highestSales = saveProduct("매출1등", ProductStatus.ACTIVATED);
        Product highQuantity = saveProduct("수량2등", ProductStatus.ACTIVATED);
        Product lowQuantity = saveProduct("수량3등", ProductStatus.ACTIVATED);
        Product fourth = saveProduct("4등", ProductStatus.ACTIVATED);
        Product fifth = saveProduct("5등", ProductStatus.ACTIVATED);
        Product sixth = saveProduct("6등", ProductStatus.ACTIVATED);

        saveOrderWithProduct(OrderStatus.PURCHASED, highestSales, 50_000L, 2L, todayAt(10));
        saveOrderWithProduct(OrderStatus.PURCHASED, highQuantity, 10_000L, 3L, todayAt(10));
        saveOrderWithProduct(OrderStatus.PURCHASED, lowQuantity, 15_000L, 2L, todayAt(10));
        saveOrderWithProduct(OrderStatus.PURCHASED, fourth, 10_000L, 2L, todayAt(10));
        saveOrderWithProduct(OrderStatus.PURCHASED, fifth, 10_000L, 1L, todayAt(10));
        saveOrderWithProduct(OrderStatus.PURCHASED, sixth, 5_000L, 1L, todayAt(10));

        AdminDashboardResponse response = dashboard();

        assertThat(response.topProducts()).hasSize(5);
        assertThat(response.topProducts())
                .extracting("productId")
                .containsExactly(
                        highestSales.getId(),
                        highQuantity.getId(),
                        lowQuantity.getId(),
                        fourth.getId(),
                        fifth.getId()
                );
    }

    @Test
    @DisplayName("OrderProduct.productId가 null인 데이터는 인기 상품 집계에서 제외한다")
    void it_excludes_order_products_without_product_id_from_top_products() {
        Product product = saveProduct("집계상품", ProductStatus.ACTIVATED);
        saveOrderWithProduct(OrderStatus.PURCHASED, product, 10_000L, 1L, todayAt(10));
        saveOrder(OrderStatus.PURCHASED, 100_000L, 1L, todayAt(10), null);

        AdminDashboardResponse response = dashboard();

        assertThat(response.topProducts()).hasSize(1);
        assertThat(response.topProducts().get(0).productId()).isEqualTo(product.getId());
    }

    private AdminDashboardResponse dashboard() {
        entityManager.flush();
        entityManager.clear();
        return adminDashboardService.getDashboardInfo();
    }

    private User saveUser(String prefix) {
        int id = ++sequence;
        return userRepository.save(new User(
                prefix + id,
                prefix + id + "@test.com",
                "1234",
                LocalDate.of(1990, 1, 1),
                Role.USER,
                Gender.MALE,
                null,
                "0100000" + String.format("%04d", id),
                "KR",
                false,
                false
        ));
    }

    private Product saveProduct(String name, ProductStatus status) {
        Product product = productRepository.save(new Product(
                name,
                1_000L,
                "요약",
                "설명",
                category
        ));
        product.switchStatus(status);
        product.setThumbnail(name + ".png");
        return product;
    }

    private Order saveOrder(OrderStatus status, Long price, Long amount, LocalDateTime createdAt) {
        return saveOrder(status, price, amount, createdAt, saveProduct("상품" + (++sequence), ProductStatus.ACTIVATED).getId());
    }

    private Order saveOrder(OrderStatus status, Long price, Long amount, LocalDateTime createdAt, Long productId) {
        Order order = orderRepository.save(new Order(user, 0L, "주소", "12345", "수령자", null, "01012345678"));
        OrderProduct orderProduct = orderProductRepository.save(new OrderProduct("상품" + sequence, amount, price, productId, "옵션", order));
        order.setOrderProduct(orderProduct);
        order.switchStatus(status);
        setOrderCreatedAt(order, createdAt);
        return order;
    }

    private void saveOrderWithProduct(OrderStatus status, Product product, Long price, Long amount, LocalDateTime createdAt) {
        Order order = orderRepository.save(new Order(user, 0L, "주소", "12345", "수령자", null, "01012345678"));
        OrderProduct orderProduct = orderProductRepository.save(new OrderProduct(product.getName(), amount, price, product.getId(), "옵션", order));
        order.setOrderProduct(orderProduct);
        order.switchStatus(status);
        setOrderCreatedAt(order, createdAt);
    }

    private void setOrderCreatedAt(Order order, LocalDateTime createdAt) {
        updateDateTimeField(Order.class, order.getId(), "createdAt", createdAt);
    }

    private void setUserCreatedAt(User user, LocalDateTime createdAt) {
        updateDateTimeField(User.class, user.getId(), "createdAt", createdAt);
    }

    private void setUserDeletedAt(User user, LocalDateTime deletedAt) {
        updateDateTimeField(User.class, user.getId(), "deletedAt", deletedAt);
    }

    private <T> void updateDateTimeField(
            Class<T> entityClass,
            Long id,
            String fieldName,
            LocalDateTime value
    ) {
        entityManager.flush();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> update = criteriaBuilder.createCriteriaUpdate(entityClass);
        Root<T> root = update.from(entityClass);
        Path<LocalDateTime> field = root.get(fieldName);
        Path<Long> idPath = root.get("id");

        update.set(field, value);
        update.where(criteriaBuilder.equal(idPath, id));

        entityManager.createQuery(update).executeUpdate();
        entityManager.clear();
    }

    private LocalDateTime todayAt(int hour) {
        return LocalDate.now().atTime(hour, 0);
    }

    private LocalDateTime yesterdayAt(int hour) {
        return LocalDate.now().minusDays(1).atTime(hour, 0);
    }
}
