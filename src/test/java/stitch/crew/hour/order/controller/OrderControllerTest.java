package stitch.crew.hour.order.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.OrderCreateFromCartRequest;
import stitch.crew.hour.order.dto.OrderCreateFromProductRequest;
import stitch.crew.hour.order.dto.OrderDetailResponse;
import stitch.crew.hour.orderproduct.dto.OrderProductCreateRequest;
import stitch.crew.hour.orderproduct.dto.OrderProductDetailResponse;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class OrderControllerTest {

    final String BASE_URL = "/api/orders";

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
    @DisplayName("Describe : POST /api/orders/product")
    class Describe_Create_Order{
        OrderCreateFromProductRequest requestFromProduct;

        @BeforeEach
        void setUp(){
            requestFromProduct = new OrderCreateFromProductRequest(
                    testProduct.getId(),
                    2L,
                    "옵션",
                    "원주시",
                    "26421312",
                    "이정수",
                    "요청이에용",
                    "01041245512"
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 성공적으로 생성 후 201을 반환")
            void It_성공적으로_주문_생성() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                String json = objectMapper.writeValueAsString(requestFromProduct);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL + "/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())
                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.receiverName").value(requestFromProduct.receiverName()));
            }

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("It : ReceiverName이 공백인 경우 Orderer Name으로 설정 및 주문을 성공적으로 생성 후 201을 반환")
            void It_ReceiverName이_공백_이더라도_성공적으로_주문_생성(String name) throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                OrderCreateFromProductRequest testOrderRequest = new OrderCreateFromProductRequest(
                        testProduct.getId(),
                        2L,
                        "옵션",
                        "원주시",
                        "26421312",
                        "",
                        "요청이에용",
                        "01041245512"
                );

                String json = objectMapper.writeValueAsString(testOrderRequest);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL + "/product")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())
                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.ordererName").value(testUser.getUserName()));
            }
        }
    }

    @Nested
    @DisplayName("Describe : POST /api/orders/cart")
    class Describe_Create_Order_By_Cart{
        Category testCategory;
        Product testProduct;
        Cart testCart;
        CartProduct testCartProduct;

        OrderCreateFromCartRequest requestFromCart;

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

            requestFromCart = new OrderCreateFromCartRequest(
                    "주소",
                    "26331",
                    "이정수",
                    "요청사황",
                    "01041245512"
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_valid_data{

            @Test
            @DisplayName("It : 주문을 성공적으로 생성 후 201을 반환")
            void It_성공적으로_주문_생성() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);


                String json = objectMapper.writeValueAsString(requestFromCart);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL + "/cart")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())

                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.ORDER_CREATED_SUCCESS.getSuccessMessage()))
                        .andExpect(jsonPath("$.data.orderProducts[0].productId").value(testProduct.getId()));
            }

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("It : ReceiverName이 공백인 경우 Orderer Name으로 설정 및 주문을 성공적으로 생성 후 201을 반환")
            void It_ReceiverName이_공백_이더라도_성공적으로_주문_생성(String name) throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                OrderCreateFromCartRequest testOrderRequest = new OrderCreateFromCartRequest(
                        "주소",
                        "26331",
                        name,
                        "요청사황",
                        "01041245512"
                );

                String json = objectMapper.writeValueAsString(testOrderRequest);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL + "/cart")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        ).andDo(print())
                        // then
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.ordererName").value(testUser.getUserName()));
            }
        }
    }

}
