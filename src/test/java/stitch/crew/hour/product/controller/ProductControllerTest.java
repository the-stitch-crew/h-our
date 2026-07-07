package stitch.crew.hour.product.controller;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanRegistrarDslMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductDetailsResponse;
import stitch.crew.hour.product.dto.ProductSearchResponse;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    final String BASE_URL = "/api/products";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
    void setUp() {
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
    @DisplayName("Describe : GET /api/products/{productId}")
    class Describe_getProduct{

        @Nested
        @DisplayName("Context : 올바른 인자가 주어진 경우")
        class Context_with_Valid_Data{

            @Test
            @DisplayName("It : 성공적으로 단건조회를 수행")
            void It_상품을_정상적으로_조회() throws Exception {

                // when
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL + "/%s".formatted(testProduct.getId()))
                        ).andDo(print())
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();

                String json = response.getContentAsString();
                ProductDetailsResponse responseData = objectMapper.readValue(
                        json,
                        new TypeReference<ApiResponses<ProductDetailsResponse>>() {
                        }
                ).data();

                Assertions.assertThat(responseData.productId()).isEqualTo(testProduct.getId());
                Assertions.assertThat(responseData.productStatus()).isEqualTo(testProduct.getStatus().name());
            }
        }
    }

    @Nested
    @DisplayName("Describe : GET /api/products")
    class Describe_getProducts{

        Paging testPaging;

        @BeforeEach
        void setUp(){
            testPaging = new Paging(0,20);
        }

        @Nested
        @DisplayName("Context : 올바른 인자가 주어진 경우")
        class Context_with_Valid_Data{

            @Test
            @DisplayName("It : 해당 카테고리명의 상품을 성공적으로 다건조회를 수행")
            void It_카테고리_해당하는_상품을_정상적으로_조회() throws Exception {

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                                        .param("categoryName",  testCategory.getName())
                                        .param("page",  "0")
                                        .param("size", "20")
                        ).andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.content[0].productId").value(testProduct.getId()))
                        .andExpect(jsonPath("$.data.numberOfElements").value(1));
                ;
            }

            @Test
            @DisplayName("It : 카테고리가 없어도 상품을 성공적으로 다건조회를 수행")
            void It_카테고리_없이_상품을_정상적으로_조회() throws Exception {

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                                        .param("page",  "0")
                                        .param("size", "20")
                        ).andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.content[0].productId").value(testProduct.getId()))
                        .andExpect(jsonPath("$.data.numberOfElements").value(1));
                ;
            }

            @Test
            @DisplayName("It : 다른 카테고리를 검색 시 상품을 검색할 수 없음")
            void It_카테고리_다른거_상품_조회__실패() throws Exception {

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                                        .param("categoryName",  "없는 카테고리")
                                        .param("page",  "0")
                                        .param("size", "20")
                        ).andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.numberOfElements").value(0));
            }

            @Test
            @DisplayName("It : 삭제된 상품을 검색할 수 없음")
            void It_삭제된거_상품_조회__실패() throws Exception {
                // given
                testProduct.switchStatus(ProductStatus.DELETED);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.get(BASE_URL)
                                        .param("page",  "0")
                                        .param("size", "20")
                        ).andDo(print())
                        // then
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.data.numberOfElements").value(0));
            }
        }
    }
}

