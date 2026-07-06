package stitch.crew.hour.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CategoryAdminController 클래스의")
class CategoryAdminControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    private CategoryService categoryService;

    String name;
    String thumbnail;
    MultipartFile file;

    @Nested
    @DisplayName("Discribe: POST / 엔드포인트는")
    class saveCategory {
        CategoryRequest request;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                name = "거거거거";
                thumbnail="ㅠㅠㅠㅠㅠ";
                request = new CategoryRequest(name);
            }

            @Test
            @DisplayName("It : 201 상태와 성공 메시지를 반환한다")
            void it_return_201_created_and_success_message() throws Exception {
                //given
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                        post("/api/admin/categories")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(request))
                )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_CREATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_CREATED.getSuccessMessage()))
                        .andDo(print());
            }

            @Test
            @DisplayName("It : 썸네일이 빈값이어도 201 상태와 성공 메시지를 반환한다")
            void it_return_201_created_and_success_message_if_enmpty_thumbnail() throws Exception {
                //given
                thumbnail="";
                request = new CategoryRequest(name);
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                post("/api/admin/categories")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_CREATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_CREATED.getSuccessMessage()))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("Context: name의 형식이 맞지 않으면")
        class Context_with_unavailable_name {
            @BeforeEach
            void setUp() {
                thumbnail="ㅠㅠㅠㅠㅠ";
            }

            @Test
            @DisplayName("(이름이 null일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
                //given
                name = null;
                request = new CategoryRequest(name);
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                post("/api/admin/categories")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(이름이 empty일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_empty() throws Exception {
                //given
                name = "";
                request = new CategoryRequest(name);
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                post("/api/admin/categories")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."))
                        .andDo(print());
            }

            @Test
            @DisplayName("(이름이 20자를 넘을때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_greater_20() throws Exception {
                //given
                name="가나다라마바사아자차카타파하가나다라마바사아자";
                request = new CategoryRequest(name);
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                post("/api/admin/categories")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 최대 20자입니다."))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: thumbnail의 형식이 맞지 않으면")
        class Context_with_unavailable_thumbnail {
            @BeforeEach
            void setUp() {
                name = "거거거거";
                thumbnail = null;
                request = new CategoryRequest(name);
            }

            @Test
            @DisplayName("(썸네일이 null일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
                //given
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                post("/api/admin/categories")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("썸네일은 null이 허용되지 않습니다."))
                        .andDo(print());
            }
        }
    }
    @Nested
    @DisplayName("Discribe: PATCH /{categoryId} 엔드포인트는")
    class updateCategory {
        Long categoryId;
        CategoryRequest request;
        String name2;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                categoryId = 1L;
                name = "거거거거";
                name2 = "거거거거2";
                thumbnail="ㅠㅠㅠㅠㅠ";
                request = new CategoryRequest(name2);
            }

            @Test
            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message() throws Exception {
                //given
                doNothing().when(categoryService).updateCategory(categoryId, request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/" + categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()))
                        .andDo(print());
            }

            @Test
            @DisplayName("It : 썸네일이 빈값이어도 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message_if_enmpty_thumbnail() throws Exception {
                //given
                thumbnail="";
                request = new CategoryRequest(name2);
                doNothing().when(categoryService).updateCategory(categoryId, request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/" + categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("Context: name의 형식이 맞지 않으면")
        class Context_with_unavailable_name {
            @BeforeEach
            void setUp() {
                categoryId = 1L;
                thumbnail="ㅠㅠㅠㅠㅠ";
            }

            @Test
            @DisplayName("(이름이 null일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
                //given
                name2 = null;
                request = new CategoryRequest(name2);
                doNothing().when(categoryService).updateCategory(categoryId, request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/" + categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."));
            }

            @Test
            @DisplayName("(이름이 empty일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_empty() throws Exception {
                //given
                name2 = "";
                request = new CategoryRequest(name2);
                doNothing().when(categoryService).updateCategory(categoryId, request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/" + categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."))
                        .andDo(print());
            }

            @Test
            @DisplayName("(이름이 20자를 넘을때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_greater_20() throws Exception {
                //given
                name2="가나다라마바사아자차카타파하가나다라마바사아자";
                request = new CategoryRequest(name2);
                doNothing().when(categoryService).save(request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/" + categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 최대 20자입니다."))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: thumbnail의 형식이 맞지 않으면")
        class Context_with_unavailable_thumbnail {
            @BeforeEach
            void setUp() {
                categoryId = 1L;
                name2 = "거거거거";
                thumbnail = null;
                request = new CategoryRequest(name2);
            }

            @Test
            @DisplayName("(썸네일이 null일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
                //given
                doNothing().when(categoryService).updateCategory(categoryId, request, file);

                //when-then
                mockMvc.perform(
                                patch("/api/admin/categories/{categoryId}", categoryId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("썸네일은 null이 허용되지 않습니다."))
                        .andDo(print());
            }
        }
    }
    @Nested
    @DisplayName("Discribe: DELETE /{categoryId} 엔드포인트는")
    class deleteCategory {
        Long categoryId = 1L;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
            }

            @Test
            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message() throws Exception {
                //given
                doNothing().when(categoryService).deleteCategory(categoryId);

                //when-then
                mockMvc.perform(
                                delete("/api/admin/categories/{categoryId}", categoryId)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_DELETED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_DELETED.getSuccessMessage()))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: 유효하지 않은 id가 주어지면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setUp() {
            }

            @Test
            @DisplayName("It : 404 상태와 실패 메시지를 반환한다")
            void it_return_404_not_found_and_fail_message() throws Exception {
                //given
                doThrow(new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                        .when(categoryService)
                        .deleteCategory(categoryId);

                //when-then
                mockMvc.perform(
                                delete("/api/admin/categories/{categoryId}", categoryId)
                                        .with(csrf())
                        )
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(ErrorCode.CATEGORY_NOT_FOUND.getMessage()))
                        .andDo(print());
            }
        }
    }
}