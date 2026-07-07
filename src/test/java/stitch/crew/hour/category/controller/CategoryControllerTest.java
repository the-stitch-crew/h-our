package stitch.crew.hour.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategoryResponse;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CategoryController 클래스의")
class CategoryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    String name;
    String thumbnail;

    String email = "newgamer@test.com";
    String email2 = "newgamer2@test.com";

    TestingAuthenticationToken adminAuthentication = TestUtil.createAdminAuthentication(email);
    TestingAuthenticationToken userAuthentication = TestUtil.createUserAuthentication(email2);

    @Nested
    @DisplayName("Discribe: GET / 엔드포인트는")
    class getCategories {
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            String name2;
            Category category1;
            Category category2;
            @BeforeEach
            void setUp() {
                name = "거거거거";
                name2 = "거거거거2";
                thumbnail="ㅠㅠㅠㅠㅠ";
                category1 = new Category(name);
                category2 = new Category(name2);

            }
            @Test
            @DisplayName("It : 200 상태와 성공 메시지, 데이터를 반환한다")
            void it_return_200_ok_and_success_message_and_data() throws Exception {
                //given
                given(categoryService.getCategories()).willReturn(
                        Arrays.asList(CategoryResponse.from(category1, null),CategoryResponse.from(category2, null)));

                //when-then
                mockMvc.perform(
                                get("/api/categories")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_READ.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_READ.getSuccessMessage()))
                        .andExpect(jsonPath("$.data[0].name").value(name))
                        .andExpect(jsonPath("$.data[1].name").value(name2))
                        .andDo(print());
            }

        }
    }

}