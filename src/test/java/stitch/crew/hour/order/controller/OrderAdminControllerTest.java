package stitch.crew.hour.order.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.repository.CartRepository;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.cartproduct.repository.CartProductRepository;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.Order;
import stitch.crew.hour.order.dto.OrderCreateFromCartRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.repository.OrderBoundaryRepository;
import stitch.crew.hour.order.service.OrderService;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class OrderAdminControllerTest {

    final String BASE_URL = "/api/admin/orders";

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
    OrderService orderService;
    @Autowired
    OrderBoundaryRepository orderBoundaryRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    User testUser;

    TestingAuthenticationToken token;

    Category testCategory;
    Product testProduct;

    @BeforeEach
    void setUp() {
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

        token = new TestingAuthenticationToken(
                CurrentUser.from(testUser),
                null,
                Role.ADMIN.getValue()
        );
    }


    @Nested
    @DisplayName("Describe : PATCH /api/admin/orders/{orderNumber}/indelivery")
    class Describe_setOrderInDelivery{

        Cart testCart;
        OrderCreateResponse orderFromCart;

        @BeforeEach
        void setUp(){

            // given
            testCart = cartRepository.save(new Cart(testUser));
            SecurityContextHolder.getContext().setAuthentication(token);

            cartProductRepository.save(
                    new CartProduct(
                            testCart,
                            testProduct,
                            2L
                    )
            );

            OrderCreateFromCartRequest requestFromCart = new OrderCreateFromCartRequest(
                    "주소" ,
                    "26331",
                    "이정수" ,
                    "요청사황",
                    "01041245512"
            );
            orderFromCart = orderService.createOrderFromCart(
                    testUser.getId(),
                    requestFromCart
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 배송중 상태로 전환")
            void It_성공적으로_배송중_전환() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/indelivery"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_IN_DELIVERY.getSuccessMessage()));
                Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderFromCart.orderNumber());
                Assertions.assertThat(foundedOrder.getOrderStatus()).isEqualTo(OrderStatus.IN_DELIVERY);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 권한이 주어진 경우")
        class Context_with_Invalid_Authority{

            @Test
            @DisplayName("It : 어드민 권한이 없으므로 403을 반환")
            void It_배송중_전환_실패() throws Exception {
                // given
                token = new TestingAuthenticationToken(
                        CurrentUser.from(testUser),
                        null,
                        Role.USER.getValue()
                );
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/indelivery"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
            }
        }
    }

    @Nested
    @DisplayName("Describe : PATCH /api/admin/orders/{orderNumber}/delivered")
    class Describe_setOrderDelivered{

        Cart testCart;
        OrderCreateResponse orderFromCart;

        @BeforeEach
        void setUp(){

            // given
            testCart = cartRepository.save(new Cart(testUser));
            SecurityContextHolder.getContext().setAuthentication(token);

            cartProductRepository.save(
                    new CartProduct(
                            testCart,
                            testProduct,
                            2L
                    )
            );

            OrderCreateFromCartRequest requestFromCart = new OrderCreateFromCartRequest(
                    "주소" ,
                    "26331",
                    "이정수" ,
                    "요청사황",
                    "01041245512"
            );
            orderFromCart = orderService.createOrderFromCart(
                    testUser.getId(),
                    requestFromCart
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 배송완료 상태로 전환")
            void It_성공적으로_배송완료_전환() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/delivered"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_DELIVERED.getSuccessMessage()));
                Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderFromCart.orderNumber());
                Assertions.assertThat(foundedOrder.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 권한이 주어진 경우")
        class Context_with_Invalid_Authority{

            @Test
            @DisplayName("It : 어드민 권한이 없으므로 403을 반환")
            void It_배송중_전환_실패() throws Exception {
                // given
                token = new TestingAuthenticationToken(
                        CurrentUser.from(testUser),
                        null,
                        Role.USER.getValue()
                );
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/delivered"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
            }
        }
    }

    @Nested
    @DisplayName("Describe : PATCH /api/admin/orders/{orderNumber}/complete")
    class Describe_setOrderComplete{

        Cart testCart;
        OrderCreateResponse orderFromCart;

        @BeforeEach
        void setUp(){

            // given
            testCart = cartRepository.save(new Cart(testUser));
            SecurityContextHolder.getContext().setAuthentication(token);

            cartProductRepository.save(
                    new CartProduct(
                            testCart,
                            testProduct,
                            2L
                    )
            );

            OrderCreateFromCartRequest requestFromCart = new OrderCreateFromCartRequest(
                    "주소" ,
                    "26331",
                    "이정수" ,
                    "요청사황",
                    "01041245512"
            );
            orderFromCart = orderService.createOrderFromCart(
                    testUser.getId(),
                    requestFromCart
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 주문완료 상태로 전환")
            void It_성공적으로_주문완료_전환() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/complete"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_COMPLETE.getSuccessMessage()));
                Order foundedOrder = orderBoundaryRepository.findByOrderNumberOrThrow(orderFromCart.orderNumber());
                Assertions.assertThat(foundedOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 권한이 주어진 경우")
        class Context_with_Invalid_Authority{

            @Test
            @DisplayName("It : 어드민 권한이 없으므로 403을 반환")
            void It_배송중_전환_실패() throws Exception {
                // given
                token = new TestingAuthenticationToken(
                        CurrentUser.from(testUser),
                        null,
                        Role.USER.getValue()
                );
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(BASE_URL + "/%s/complete"
                                        .formatted(orderFromCart.orderNumber()))
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
            }
        }
    }
}