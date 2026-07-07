package stitch.crew.hour.cart.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.dto.CartDetailResponse;
import stitch.crew.hour.cart.dto.AddCartProductRequest;
import stitch.crew.hour.cart.dto.UpdateCartProductRequest;
import stitch.crew.hour.cart.repository.CartCombineRepository;
import stitch.crew.hour.cart.repository.CartRepository;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class CartServiceTest {

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
    @Autowired
    private CartRepository cartRepository;

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
    }

    @Nested
    @DisplayName("Describe : createCart() 에 대해서")
    class Describe_Create_Cart{
        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{
            @Test
            @DisplayName("It : 장바구니 생성 성공")
            void It_장바구니_생성_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                CartDetailResponse cartResponse = cartService.createCart(testUser.getId());


                // then
                Cart foundedCart = cartCombineRepository.findCartByIdOrThrow(cartResponse.cartId());
                Assertions.assertThat(foundedCart.getId()).isEqualTo(cartResponse.cartId());
                User foundedUser = userRepository.findByIdOrthrow(testUser.getId());
                Assertions.assertThat(foundedUser.getCart()).isNotNull();
            }
        }
        @Nested
        @DisplayName("Context : 올바르지 않은 정보가 주어진 경우")
        class Context_with_Invalid_Data{
            @Test
            @DisplayName("It : 이미 장바구니가 존재하는 경우 장바구니 생성 실패")
            void It_장바구니_생성_실패(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse cartResponse = cartService.createCart(testUser.getId());

                // when
                Assertions.assertThatThrownBy(
                        ()-> cartService.createCart(testUser.getId())
                )
                        // then
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CART_ALREADY_EXISTS.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Describe : getCartByMe() 에 대해서")
    class Describe_Retrive_Cart_By_Me{
        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{
            @Test
            @DisplayName("It : 장바구니 조회 성공")
            void It_장바구니_조회_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse firstResponse = cartService.createCart(testUser.getId());

                // when
                CartDetailResponse secondResponse = cartService.getCartByMe(testUser.getId());

                // then
                Assertions.assertThat(firstResponse.cartId()).isEqualTo(secondResponse.cartId());
            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 정보가 주어진 경우")
        class Context_with_Invalid_Data{
            @Test
            @DisplayName("It : 장바구니가 없는 유저가 조회 시 조회 실패")
            void It_장바구니_없어도_장바구니_생성_후__조회_실패(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                Assertions.assertThatThrownBy(
                        ()-> cartService.getCartByMe(testUser.getId())
                ).isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NO_CART.getMessage());
            }

        }
    }


    @Nested
    @DisplayName("Describe : addCartProductToCart() 에 대해서")
    class Describe_addCartProduct{
        Category testCategory;
        Product testProduct;

        @BeforeEach
        void setUp(){
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
        }

        @Nested
        @DisplayName("Context : 올바른 정보가 주어진 경우")
        class Context_with_Valid_Data{
            @Test
            @DisplayName("It : 장바구니에 상품 추가 성공")
            void It_장바구니_상품_추가_성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse cart = cartService.createCart(testUser.getId());

                // when
                CartDetailResponse response = cartService.addCartProductToCart(
                        testUser.getId(),
                        cart.cartId(),
                        new AddCartProductRequest(
                            testProduct.getId(),
                            2L
                        )
                );

                // then
                Cart founded = cartRepository.findByIdOrThrow(cart.cartId());
                Assertions.assertThat(founded.getCartProducts().getFirst().getId()).isEqualTo(response.products().getFirst().cartProductId());
                Assertions.assertThat(response.totalPrice()).isEqualTo(4000L);
            }
        }

    }

    @Nested
    @DisplayName("Describe : updateCartProduct() 에 대해서")
    class Describe_updateCartProduct{
        Category testCategory;
        Product testProduct;

        @BeforeEach
        void setUp(){
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
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_Valid_Data{

            @Test
            @DisplayName("It: 1개 이상인 경우 상품의 수량을 수정")
            void It_상품의_수량을_수정(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse cart = cartService.createCart(testUser.getId());
                CartDetailResponse cartResponse = cartService.addCartProductToCart(
                        testUser.getId(),
                        cart.cartId(),
                        new AddCartProductRequest(
                                testProduct.getId(),
                                2L
                        )
                );

                // when
                CartDetailResponse response = cartService.updateCartProduct(
                        testUser.getId(),
                        cart.cartId(),
                        new UpdateCartProductRequest(
                                cartResponse.products().getFirst().cartProductId(),
                                1L
                        )
                );

                // then
                Cart foundedCart = cartRepository.findByIdOrThrow(cart.cartId());
                Assertions.assertThat(foundedCart.getCartProducts().size()).isEqualTo(1);
            }

            @Test
            @DisplayName("It: 0개인 경우 장바구니 상품을 삭제")
            void It_상품을_삭제(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse cart = cartService.createCart(testUser.getId());
                CartDetailResponse cartResponse = cartService.addCartProductToCart(
                        testUser.getId(),
                        cart.cartId(),
                        new AddCartProductRequest(
                                testProduct.getId(),
                                2L
                        )
                );

                // when
                cartService.updateCartProduct(
                        testUser.getId(),
                        cart.cartId(),
                        new UpdateCartProductRequest(
                                cartResponse.products().getFirst().cartProductId(),
                                0L
                        )
                );

                // then
                Cart foundedCart = cartRepository.findByIdOrThrow(cart.cartId());
                Assertions.assertThat(foundedCart.getCartProducts().size()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Describe : deleteCart() 에 대해서")
    class Describe_deleteCart{
        Category testCategory;
        Product testProduct;

        @BeforeEach
        void setUp(){
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
        }

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_Valid_Data{

            @Test
            @DisplayName("It: 장바구니 및 장바구니 상품 삭제 성공")
            void It_상품의_수량을_수정(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                CartDetailResponse cart = cartService.createCart(testUser.getId());
                CartDetailResponse cartResponse = cartService.addCartProductToCart(
                        testUser.getId(),
                        cart.cartId(),
                        new AddCartProductRequest(
                                testProduct.getId(),
                                2L
                        )
                );

                // when
                cartService.deleteCart(
                        testUser.getId(),
                        cart.cartId()
                );

                // then
                Assertions.assertThatThrownBy(
                        ()->cartCombineRepository.findCartByIdOrThrow(cart.cartId())
                ).isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CART_NOT_FOUND.getMessage());

                Assertions.assertThatThrownBy(
                                ()->cartCombineRepository.findCartProductByIdOrThrow(cartResponse.products().getFirst().cartProductId())
                        ).isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.CARTPRODUCT_NOT_FOUNT.getMessage());
            }

        }

    }

}