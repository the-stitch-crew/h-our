package stitch.crew.hour.product.service;

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
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductDetailsResponse;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;

import java.time.LocalDate;


@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    ProductService service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    User testUser;
    TestingAuthenticationToken token;
    Category testCategory;
    Product testProduct;
    @Autowired
    private ProductService productService;

    @BeforeEach
    void setUp(){
        testUser = userRepository.save(
                new User(
                        "이름",
                        "wjdtn747@naver.com",
                        "1234",
                        LocalDate.now(),
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
                "ROLE_USER"
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
    }

    @Nested
    @DisplayName("Describe : createProduct()에")
    class Describe_createProduct{

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_Valid_Data{
            @Test
            @DisplayName("It : 상품을 정상적으로 생성")
            void It_상품_생성__성공(){
                // given
                testUser.switchAdmin();
                SecurityContextHolder.getContext().setAuthentication(token);
                ProductCreateRequest request = TestUtil.productCreateRequest(
                        "요약",
                        "설명"
                );

                // when
                ProductCreateResponse product = service.createProduct(
                        testUser.getId(),
                        testCategory.getId(),
                        request
                );

                // then
                Assertions.assertThat(product.productName()).isEqualTo(request.name());
                Assertions.assertThat(product.price()).isEqualTo(request.price());
            }
        }

        @Nested
        @DisplayName("Context : 적합한 권한이 없는 경우")
        class Context_with_InValid_Authorities{
            @Test
            @DisplayName("It : 어드민이 아닌 경우 상품 생성 실패")
            void It_상품_생성__성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);
                ProductCreateRequest request = TestUtil.productCreateRequest(
                        "요약",
                        "설명"
                );

                // when
                Assertions.assertThatThrownBy(
                        ()-> service.createProduct(
                                testUser.getId(),
                                testCategory.getId(),
                                request
                        )
                ).isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.NOT_ADMIN.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Describe : getProductDetail()에")
    class Describe_getProductDetail{

        @Nested
        @DisplayName("Context : 올바른 데이터가 주어진 경우")
        class Context_with_Valid_Data{

            @Test
            @DisplayName("It : 상품 단건 조회 성공")
            void It_상품_단건조회__성공(){
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                // when
                ProductDetailsResponse founded = productService.getProductDetail(testProduct.getId());

                // then
                Assertions.assertThat(founded.name()).isEqualTo(founded.name());
            }

        }

    }

}