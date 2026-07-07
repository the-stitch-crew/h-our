package stitch.crew.hour.cart.controller;

import com.sun.net.httpserver.Authenticator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.dto.AddCartProductRequest;
import stitch.crew.hour.cart.dto.UpdateCartProductRequest;
import stitch.crew.hour.cart.repository.CartCombineRepository;
import stitch.crew.hour.cart.repository.CartCombineRepositoryImpl;
import stitch.crew.hour.cart.repository.CartRepository;
import stitch.crew.hour.cart.service.CartService;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductCreateRequest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerTest {

    final String BASE_URL = "/api/carts";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartCombineRepository cartCombineRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CartService cartService;

    TestingAuthenticationToken token;

    User testUser;

    Cart testCart;

    Category testCategory;

    CartProduct testCartProduct;

    Product testProduct;
    @Autowired
    private CartCombineRepositoryImpl cartCombineRepositoryImpl;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                new User(
                        "이름",
                        "wjdtn747@naver.com",
                        "1234",
                        LocalDate.now(),
                        Role.ADMIN,
                        Gender.MALE,
                        "010",
                        "?",
                        "대한민국",
                        false,
                        false
                )
        );

        token = new TestingAuthenticationToken(
                CurrentUser.from(testUser),
                null,
                Role.ADMIN.getValue()
        );

        testCart = cartCombineRepository.saveCart(
                new Cart(testUser)
        );

        testCategory = categoryRepository.save(
                new Category("카테고리명", "썸네일")
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

        testCartProduct = cartCombineRepository.saveCartProduct(
                new CartProduct(
                        testCart,
                        testProduct,
                        1L
                )
        );
    }

    @Nested
    @DisplayName("GET : /api/carts 에서")
    class Describe_getCart {
        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority {
            @Test
            @DisplayName("It : 장바구니 조회 성공")
            void It_장바구니_조회_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.cartId").value(testCart.getId()))
                        .andExpect(jsonPath("$.data.userId").value(testUser.getId()));
            }
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 없는 경우")
        class Context_with_InValid_Data {
            @Test
            @DisplayName("It : 장바구니가 없어 장바구니 조회 실패")
            void It_장바구니_조회_성공() throws Exception {
                // given
                User subjectUser = userRepository.save(
                        new User(
                                "이름23123",
                                "wjdtn72132147@naver.com",
                                "123123124",
                                LocalDate.now(),
                                Role.ADMIN,
                                Gender.MALE,
                                "0102331231",
                                "?3123212",
                                "대한민국",
                                false,
                                false
                        )
                );

                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.from(subjectUser),
                                null,
                                Role.USER.getValue()
                        )
                );

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.message").value(ErrorCode.NO_CART.getMessage()));
            }
        }
    }


    @Nested
    @DisplayName("POST : /api/carts 에서")
    class Describe_createCart {
        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority {
            @Test
            @DisplayName("It : 장바구니 생성 성공")
            void It_장바구니_생성_성공() throws Exception {
                // given
                User subjectUser = userRepository.save(
                        new User(
                                "이름23123",
                                "wjdtn72132147@naver.com",
                                "123123124",
                                LocalDate.now(),
                                Role.ADMIN,
                                Gender.MALE,
                                "0102331231",
                                "?3123212",
                                "대한민국",
                                false,
                                false
                        )
                );

                SecurityContextHolder.getContext().setAuthentication(
                        new TestingAuthenticationToken(
                                CurrentUser.from(subjectUser),
                                null,
                                Role.USER.getValue()
                        )
                );

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true));
            }
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 없는 경우")
        class Context_with_InValid_Data {
            @Test
            @DisplayName("It : 장바구니가 이미 존재 시 장바구니 조회 실패")
            void It_장바구니_조회_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URL)
                )
                        .andDo(print())

                        // then
                        .andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.message").value(ErrorCode.CART_ALREADY_EXISTS.getMessage()));
            }
        }
    }


    @Nested
    @DisplayName("POST : /api/carts/{cartId} 에서")
    class Describe_addProductToCart {
        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority {
            @Test
            @DisplayName("It : 장바구니에 상품 추가 성공")
            void It_장바구니_상품_추가_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                Product subjectProduct = productRepository.save(
                        new Product(
                                "테스트용 상품2",
                                3000L,
                                "상품요약1",
                                "설명글1",
                                testCategory
                        )
                );

                String json = objectMapper.writeValueAsString(
                        new AddCartProductRequest(
                                subjectProduct.getId(),
                                2L
                        )
                );

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL + "/%s".formatted(testCart.getId()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CART_ADDITION_SUCCESS.getSuccessMessage()));
            }
        }
    }

    @Nested
    @DisplayName("PUT : /api/carts/{cartId} 에서")
    class Describe_updateCartProduct {
        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority {
            @Test
            @DisplayName("It : 장바구니에 상품 수정 성공")
            void It_장바구니_상품_수정_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                String json = objectMapper.writeValueAsString(
                        new UpdateCartProductRequest(
                                testCartProduct.getId(),
                                50L
                        )
                );

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.put(BASE_URL + "/%s".formatted(testCart.getId()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CART_UPDATE_SUCCESS.getSuccessMessage()))
                        .andExpect(jsonPath("$.data.products[0].amount").value(50L));
            }
            @Test
            @DisplayName("It : 장바구니에 상품 0으로 설정하여 삭제 성공")
            void It_장바구니_상품_0으로_수정_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                String json = objectMapper.writeValueAsString(
                        new UpdateCartProductRequest(
                                testCartProduct.getId(),
                                0L
                        )
                );

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.put(BASE_URL + "/%s".formatted(testCart.getId()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CART_UPDATE_SUCCESS.getSuccessMessage()));
            }
        }
    }

    @Nested
    @DisplayName("DELETE : /api/carts/{cartId} 에서")
    class Describe_deleteCart {
        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority {
            @Test
            @DisplayName("It : 장바구니 삭제 성공")
            void It_장바구니_상품_삭제_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(BASE_URL + "/%s".formatted(testCart.getId()))
                        )
                        .andDo(print())

                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CART_DELETE_SUCCESS.getSuccessMessage()));

                Assertions.assertThatThrownBy(
                        ()->cartCombineRepositoryImpl.findCartByIdOrThrow(testCart.getId())
                ).isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CART_NOT_FOUND.getMessage());

            }

            @Test
            @DisplayName("It : 장바구니 삭제 및 재생성 성공")
            void It_장바구니_상품_삭제_및_재생성_성공() throws Exception {
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(BASE_URL + "/%s".formatted(testCart.getId()))
                        )
                        .andDo(print())

                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CART_DELETE_SUCCESS.getSuccessMessage()));

                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true));
            }
        }
    }
}