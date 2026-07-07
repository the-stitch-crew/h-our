package stitch.crew.hour.order.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.repository.CartRepository;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.cartproduct.repository.CartProductRepository;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.order.dto.OrderCreateFromCartRequest;
import stitch.crew.hour.order.dto.OrderCreateFromProductRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.dto.OrderDetailResponse;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.shippingpolicy.domain.ShippingPolicy;
import stitch.crew.hour.shippingpolicy.repository.ShippingPolicyRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("OrderService의")
class OrderServiceTest {
    @Autowired
    OrderService orderService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartProductRepository cartProductRepository;

    @Autowired
    ShippingPolicyRepository shippingPolicyRepository;


    User testUser;
    TestingAuthenticationToken token;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                new User(
                        "이름",
                        "wjdtn747@naver.com",
                        "1234",
                        LocalDate.now(),
                        Role.USER,
                        Gender.MALE,
                        "google",
                        "010",
                        "?",
                        false,
                        false
                )
        );

        token = new TestingAuthenticationToken(
                CurrentUser.from(testUser),
                null,
                Role.ADMIN.getValue()
        );
    }

    @Nested
    @DisplayName("Describe : createSingleOrder()는")
    class Describe_Create_Order{
        Category testCategory;
        Product testProduct;

        @BeforeEach
        void setUp(){
            testCategory = categoryRepository.save(
                    new Category("카테고리명")
            );

            testProduct = productRepository.save(
                    new Product(
                            "테스트용 상품",
                            2000L,
                            "상품요약",
                            "설명글",
                            testCategory
                    )
            );
        }

        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{


            @Test
            @DisplayName("It : 성공적으로 주문을 생성")
            void it_성공적으로_주문을_생성(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

                OrderCreateFromProductRequest requestFromProduct = new OrderCreateFromProductRequest(
                        testProduct.getId(),
                        2L,
                        "옵션",
                        "원주시",
                        "26421312",
                        "이정수",
                        "요청이에용",
                        "01041245512"
                );

                // when
                OrderCreateResponse order = orderService.createSingleOrder(
                        testUser.getId(),
                        requestFromProduct
                );

                // then
                Assertions.assertThat(order.orderProducts().size()).isEqualTo(1);
                Assertions.assertThat(order.totalPrice()).isEqualTo(
                        4000L + activeOrThrow.getDeliveryFee()
                );
            }

        }

    }

    @Nested
    @DisplayName("Describe : createOrderFromCart()는")
    class Describe_createOrderFromCart{
        Category testCategory;
        Product testProduct;
        Cart testCart;
        CartProduct testCartProduct;

        @BeforeEach
        void setUp(){

            testCategory = categoryRepository.save(
                    new Category("카테고리명")
            );

            testProduct = productRepository.save(
                    new Product(
                            "테스트용 상품",
                            2000L,
                            "상품요약",
                            "설명글",
                            testCategory
                    )
            );

            testCart = cartRepository.save(new Cart(testUser));

            testCartProduct = cartProductRepository.save(
                    new CartProduct(
                            testCart,
                            testProduct,
                            2L
                    )
            );
        }

        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{


            @Test
            @DisplayName("It : 성공적으로 주문을 생성")
            void it_성공적으로_주문을_생성(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

                OrderCreateFromCartRequest requestFromCart = new OrderCreateFromCartRequest(
                        "주소",
                        "26331",
                        "이정수",
                        "요청사황",
                        "01041245512"
                );

                // when
                OrderCreateResponse order = orderService.createOrderFromCart(
                        testUser.getId(),
                        requestFromCart
                );

                // then
                Assertions.assertThat(order.orderProducts().size()).isEqualTo(1);
                Assertions.assertThat(order.totalPrice()).isEqualTo(
                        4000L + activeOrThrow.getDeliveryFee()
                );
            }
        }
    }

    @Nested
    @DisplayName("Describe : getOrderDetail()는")
    class Describe_getOrderDetail{
        Category testCategory;
        Product testProduct;
        Cart testCart;
        CartProduct testCartProduct;

        @BeforeEach
        void setUp(){

            testCategory = categoryRepository.save(
                    new Category("카테고리명")
            );

            testProduct = productRepository.save(
                    new Product(
                            "테스트용 상품",
                            2000L,
                            "상품요약",
                            "설명글",
                            testCategory
                    )
            );

            testCart = cartRepository.save(new Cart(testUser));

            testCartProduct = cartProductRepository.save(
                    new CartProduct(
                            testCart,
                            testProduct,
                            2L
                    )
            );
        }

        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{


            @Test
            @DisplayName("It : 성공적으로 주문을 조회 및 200 코드 반환")
            void it_성공적으로_주문_조회(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                ShippingPolicy activeOrThrow = shippingPolicyRepository.findActiveOrThrow();

                OrderCreateFromCartRequest requestFromCart = new OrderCreateFromCartRequest(
                        "주소",
                        "26331",
                        "이정수",
                        "요청사황",
                        "01041245512"
                );
                OrderCreateResponse order = orderService.createOrderFromCart(
                        testUser.getId(),
                        requestFromCart
                );


                // when
                OrderDetailResponse foundedOrder = orderService.getOrderDetail(
                        testUser.getId(),
                        order.orderNumber()
                );

                // then
                Assertions.assertThat(order.totalPrice()).isEqualTo(foundedOrder.totalPrice());
                Assertions.assertThat(order.orderProducts().getFirst().productId())
                        .isEqualTo(foundedOrder.orderProducts().getFirst().productId());
            }
        }
    }
}
