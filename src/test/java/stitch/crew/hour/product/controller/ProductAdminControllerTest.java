package stitch.crew.hour.product.controller;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductAdminControllerTest {

    final String BASE_URL = "/api/admin/products";

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

    @BeforeEach
    void setUp() {
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
    @DisplayName("POST : /api/admin/products 에서")
    class Describe_createPost{

        ProductCreateRequest createRequest;

        @BeforeEach
        void setUp(){
            createRequest = new ProductCreateRequest(
                    "이정수",
                    2000L,
                    "요약",
                    "설명",
                    testCategory.getId()
            );
        }

        @Nested
        @DisplayName("Context : 올바른 데이터 / 권한이 부여된 경우")
        class Context_with_Valid_Data_and_Authority{

            @Test
            @DisplayName("It : Product 생성 성공")
            void It_Product_생성_성공() throws Exception {
                // given
                testUser.switchAdmin();
                SecurityContextHolder.getContext().setAuthentication(token);

                String json = objectMapper.writeValueAsString(createRequest);

                System.out.println(json);

                // when
                mockMvc.perform(
                        MockMvcRequestBuilders.post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                        .andDo(print())
                        // then
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value(SuccessCode.PRODUCT_CREATED_SUCCESS.name()))
                        .andExpect(jsonPath("$.data.productName").value("이정수"));

            }
        }

        @Nested
        @DisplayName("Context : 올바르지 않은 권한이 부여된 경우")
        class Context_with_InValid_Authority{

            @Test
            @DisplayName("It : 유저 권한으로 생성 시 실패")
            void It_Product_생성_실패() throws Exception{
                // given
                SecurityContextHolder.getContext().setAuthentication(token);

                String json = objectMapper.writeValueAsString(createRequest);

                System.out.println(json);

                // when
                mockMvc.perform(
                                MockMvcRequestBuilders.post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)
                        )
                        .andDo(print())
                        // then
                        .andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.success").value(false))
                        .andExpect(jsonPath("$.code").value(ErrorCode.NOT_ADMIN.name()));
            }

        }


    }

}