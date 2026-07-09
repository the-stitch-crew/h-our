package stitch.crew.hour.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import stitch.crew.hour.category.dto.AdminCategoryDetailResponse;
import stitch.crew.hour.category.dto.AdminCategorySearchResponse;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.service.CategoryAdminService;
import stitch.crew.hour.category.service.CategoryService;
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private CategoryAdminService categoryAdminService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockitoBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    String name;
    String thumbnail;

    String email = "newgamer@test.com";
    String email2 = "newgamer2@test.com";


    TestingAuthenticationToken adminAuthentication = TestUtil.createAdminAuthentication(email);
    TestingAuthenticationToken userAuthentication = TestUtil.createUserAuthentication(email2);

    @Nested
    @DisplayName("Describe : GET /api/admin/categories 엔드포인트는")
    class Describe_getCategories {

        @Test
        @DisplayName("It : 카테고리 목록을 반환한다")
        void It_카테고리_목록을_반환한다() throws Exception {
            // given
            Page<AdminCategorySearchResponse> response = new PageImpl<>(
                    List.of(searchResponse())
            );

            given(categoryAdminService.getCategories(0, 20, null, false)).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/admin/categories"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_READ.name()))
                    .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_READ.getSuccessMessage()))
                    .andExpect(jsonPath("$.data.content[0].categoryId").value(1L))
                    .andExpect(jsonPath("$.data.content[0].name").value("가방"))
                    .andExpect(jsonPath("$.data.content[0].totalProductCount").value(3L))
                    .andExpect(jsonPath("$.data.content[0].activeProductCount").value(1L))
                    .andExpect(jsonPath("$.data.content[0].soldOutProductCount").value(1L))
                    .andExpect(jsonPath("$.data.content[0].mainProductCount").value(1L))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Describe : GET /api/admin/categories/{categoryId} 엔드포인트는")
    class Describe_getCategory {

        @Test
        @DisplayName("It : 카테고리 상세를 반환한다")
        void It_카테고리_상세를_반환한다() throws Exception {
            // given
            Long categoryId = 1L;

            given(categoryAdminService.getCategory(categoryId)).willReturn(detailResponse());

            // when & then
            mockMvc.perform(get("/api/admin/categories/{categoryId}", categoryId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_READ.name()))
                    .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_READ.getSuccessMessage()))
                    .andExpect(jsonPath("$.data.categoryId").value(categoryId))
                    .andExpect(jsonPath("$.data.activeProductCount").value(1L))
                    .andExpect(jsonPath("$.data.soldOutProductCount").value(1L))
                    .andExpect(jsonPath("$.data.deactivatedProductCount").value(1L))
                    .andExpect(jsonPath("$.data.deletedProductCount").value(1L))
                    .andDo(print());
        }

        @Test
        @DisplayName("It : 카테고리가 존재하지 않으면 404 상태를 반환한다")
        void It_카테고리가_존재하지_않으면_404_상태를_반환한다() throws Exception {
            // given
            Long categoryId = 1L;

            willThrow(new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                    .given(categoryAdminService)
                    .getCategory(categoryId);

            // when & then
            mockMvc.perform(get("/api/admin/categories/{categoryId}", categoryId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_NOT_FOUND.name()))
                    .andExpect(jsonPath("$.message").value(ErrorCode.CATEGORY_NOT_FOUND.getMessage()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Describe : PATCH /api/admin/categories/{categoryId} 엔드포인트는")
    class Describe_updateCategory {

        @Test
        @DisplayName("It : 카테고리를 수정하고 성공 메시지를 반환한다")
        void It_카테고리를_수정하고_성공_메시지를_반환한다() throws Exception {
            // given
            Long categoryId = 1L;
            CategoryRequest request = new CategoryRequest("가방");
            MockMultipartFile requestPart = new MockMultipartFile(
                    "request",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    om.writeValueAsBytes(request)
            );

            doNothing().when(categoryService).updateCategory(categoryId, request, null);

            // when & then
            mockMvc.perform(
                            multipart("/api/admin/categories/{categoryId}", categoryId)
                                    .file(requestPart)
                                    .with(servletRequest -> {
                                        servletRequest.setMethod("PATCH");
                                        return servletRequest;
                                    })
                                    .with(csrf())
                                    .principal(adminAuthentication)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
                    .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Describe : DELETE /api/admin/categories/{categoryId} 엔드포인트는")
    class Describe_deleteCategory {

        @Test
        @DisplayName("It : 카테고리를 삭제하고 성공 메시지를 반환한다")
        void It_카테고리를_삭제하고_성공_메시지를_반환한다() throws Exception {
            // given
            Long categoryId = 1L;

            doNothing().when(categoryService).deleteCategory(categoryId);

            // when & then
            mockMvc.perform(
                            delete("/api/admin/categories/{categoryId}", categoryId)
                                    .with(csrf())
                                    .principal(adminAuthentication)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_DELETED.name()))
                    .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_DELETED.getSuccessMessage()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("Discribe: POST / 엔드포인트는")
    class saveCategory {
        CategoryRequest request;
        MockMultipartFile requestPart;
        MockMultipartFile filePart;


        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                name = "거거거거";
                request = new CategoryRequest(name);
                requestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        om.writeValueAsBytes(request)
                );
                filePart = new MockMultipartFile(
                        "file",
                        "test.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "test".getBytes()
                );
            }

            @Test
            @DisplayName("It : 201 상태와 성공 메시지를 반환한다")
            void it_return_201_created_and_success_message() throws Exception {
                //given
                doNothing().when(categoryService).save(request, filePart);

                //when-then
                mockMvc.perform(
                        multipart("/api/admin/categories")
                                .file(requestPart)
                                .file(filePart)
                                .with(csrf())
                                .principal(adminAuthentication)
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
                request = new CategoryRequest(name);
                doNothing().when(categoryService).save(request, null);

                //when-then
                mockMvc.perform(
                        multipart("/api/admin/categories")
                                .file(requestPart)
                                .with(csrf())
                                .principal(adminAuthentication)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_CREATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_CREATED.getSuccessMessage()))
                        .andDo(print());
            }
        }
//        @Nested
//        @DisplayName("Context: 인증/인가에 실패하면")
//        class Context_with_auth_fail {
//            @BeforeEach
//            void setUp() {
//                name = "거거거거";
//                request = new CategoryRequest(name);
//                requestPart = new MockMultipartFile(
//                        "request",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        om.writeValueAsBytes(request)
//                );
//                filePart = new MockMultipartFile(
//                        "file",
//                        "test.png",
//                        MediaType.IMAGE_PNG_VALUE,
//                        "test".getBytes()
//                );
//            }
//
//            @Test
//            @DisplayName("It: 로그인되지 않으면 401을 반환한다")
//            void it_return_401_when_not_authenticated() throws Exception {
//
//                mockMvc.perform(
//                                multipart("/api/admin/categories")
//                                        .file(requestPart)
//                                        .file(filePart)
//                                        .with(csrf())
//                        )
//                        .andExpect(status().isUnauthorized());
//            }
//
//            @Test
//            @DisplayName("It: 권한이 없으면 403을 반환한다")
//            void it_return_403_when_forbidden() throws Exception {
//
//                mockMvc.perform(
//                                multipart("/api/admin/categories")
//                                        .file(requestPart)
//                                        .file(filePart)
//                                        .with(csrf())
//                                        .principal(userAuthentication)
//                        )
//                        .andExpect(status().isForbidden());
//            }
//        }

        @Nested
        @DisplayName("Context: 요청 구조 오류가 발생하면")
        class Context_with_request_error {
            @BeforeEach
            void setUp() {
                name = "거거거거";
                filePart = new MockMultipartFile(
                        "file",
                        "test.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "test".getBytes()
                );
            }

            @Test
            @DisplayName("It: JSON 파싱이 실패하면 400을 반환한다")
            void it_return_400_when_json_invalid() throws Exception {

                String invalidJson = "{ name: 'broken json' }";

                MockMultipartFile badRequestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        invalidJson.getBytes()
                );

                mockMvc.perform(
                                multipart("/api/admin/categories")
                                        .file(badRequestPart)
                                        .file(filePart)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("It: request part가 없으면 400을 반환한다")
            void it_return_400_when_request_part_missing() throws Exception {

                mockMvc.perform(
                                multipart("/api/admin/categories")
                                        .file(filePart)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("Context: name의 형식이 맞지 않으면")
        class Context_with_unavailable_name {
            @BeforeEach
            void setUp() {
                filePart = new MockMultipartFile(
                        "file",
                        "test.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "test".getBytes()
                );
            }

            @Test
            @DisplayName("(이름이 null일때) It : 400 상태와 검증 메시지를 반환한다")
            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
                //given
                name = null;
                request = new CategoryRequest(name);
                requestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        om.writeValueAsBytes(request)
                );
                doNothing().when(categoryService).save(request, null);

                //when-then
                mockMvc.perform(
                        multipart("/api/admin/categories")
                                .file(requestPart)
                                .with(csrf())
                                .principal(adminAuthentication)
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
                requestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        om.writeValueAsBytes(request)
                );
                doNothing().when(categoryService).save(request, null);

                //when-then
                mockMvc.perform(
                        multipart("/api/admin/categories")
                                .file(requestPart)
                                .with(csrf())
                                .principal(adminAuthentication)
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
                requestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        om.writeValueAsBytes(request)
                );
                doNothing().when(categoryService).save(request, null);

                //when-then
                mockMvc.perform(
                                multipart("/api/admin/categories")
                                        .file(requestPart)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("name은 최대 20자입니다."))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: 비즈니스 예외가 발생하면")
        class Context_with_business_error {

            MockMultipartFile requestPart;
            MockMultipartFile filePart;

            @BeforeEach
            void setUp() {
                request = new CategoryRequest("거거거거");

                requestPart = new MockMultipartFile(
                        "request",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        om.writeValueAsBytes(request)
                );

                filePart = new MockMultipartFile(
                        "file",
                        "test.png",
                        MediaType.IMAGE_PNG_VALUE,
                        "test".getBytes()
                );
            }

            @Test
            @DisplayName("It: 이미 존재하는 카테고리면 409를 반환한다")
            void it_return_409_when_duplicate() throws Exception {

                doThrow(new BusinessException(ErrorCode.EXIST_CATEGORY))
                        .when(categoryService)
                        .save(any(), any());

                mockMvc.perform(
                                multipart("/api/admin/categories")
                                        .file(requestPart)
                                        .file(filePart)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isConflict());
            }

//            @Test
//            @DisplayName("It: 파일이 비어있으면 400을 반환한다")
//            void it_return_400_when_file_empty() throws Exception {
//
//                MockMultipartFile emptyFile = new MockMultipartFile(
//                        "file",
//                        "",
//                        MediaType.IMAGE_PNG_VALUE,
//                        new byte[0]
//                );
//
//                mockMvc.perform(
//                                multipart("/api/admin/categories")
//                                        .file(requestPart)
//                                        .file(emptyFile)
//                                        .with(csrf())
//                                        .principal(adminAuthentication)
//                        )
//                        .andExpect(status().isBadRequest());
//            }
//
//            @Test
//            @DisplayName("It: 파일 용량이 초과되면 400을 반환한다")
//            void it_return_400_when_file_size_invalid() throws Exception {
//
//                byte[] largeFile = new byte[21 * 1024 * 1024];
//
//                MockMultipartFile bigFile = new MockMultipartFile(
//                        "file",
//                        "test.png",
//                        MediaType.IMAGE_PNG_VALUE,
//                        largeFile
//                );
//
//                mockMvc.perform(
//                                multipart("/api/admin/categories")
//                                        .file(requestPart)
//                                        .file(bigFile)
//                                        .with(csrf())
//                                        .principal(adminAuthentication)
//                        )
//                        .andExpect(status().isBadRequest());
//            }
//
//            @Test
//            @DisplayName("It: 파일 확장자가 잘못되면 400을 반환한다")
//            void it_return_400_when_extension_invalid() throws Exception {
//
//                MockMultipartFile wrongFile = new MockMultipartFile(
//                        "file",
//                        "test.exe",
//                        MediaType.APPLICATION_OCTET_STREAM_VALUE,
//                        "test".getBytes()
//                );
//
//                mockMvc.perform(
//                                multipart("/api/admin/categories")
//                                        .file(requestPart)
//                                        .file(wrongFile)
//                                        .with(csrf())
//                                        .principal(adminAuthentication)
//                        )
//                        .andExpect(status().isBadRequest());
//            }
        }
    }
//    @Nested
//    @DisplayName("Discribe: PATCH /{categoryId} 엔드포인트는")
//    class updateCategory {
//        Long categoryId;
//        CategoryRequest request;
//        String name2;
//
//        @Nested
//        @DisplayName("Context: 올바른 데이터가 주어지면")
//        class Context_with_available_data {
//            @BeforeEach
//            void setUp() {
//                categoryId = 1L;
//                name = "거거거거";
//                name2 = "거거거거2";
//                thumbnail="ㅠㅠㅠㅠㅠ";
//                request = new CategoryRequest(name2);
//            }
//
//            @Test
//            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
//            void it_return_200_ok_and_success_message() throws Exception {
//                //given
//                doNothing().when(categoryService).updateCategory(categoryId, request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/" + categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
//                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()))
//                        .andDo(print());
//            }
//
//            @Test
//            @DisplayName("It : 썸네일이 빈값이어도 200 상태와 성공 메시지를 반환한다")
//            void it_return_200_ok_and_success_message_if_enmpty_thumbnail() throws Exception {
//                //given
//                thumbnail="";
//                request = new CategoryRequest(name2);
//                doNothing().when(categoryService).updateCategory(categoryId, request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/" + categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_UPDATED.name()))
//                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_UPDATED.getSuccessMessage()))
//                        .andDo(print());
//            }
//        }
//
//        @Nested
//        @DisplayName("Context: name의 형식이 맞지 않으면")
//        class Context_with_unavailable_name {
//            @BeforeEach
//            void setUp() {
//                categoryId = 1L;
//                thumbnail="ㅠㅠㅠㅠㅠ";
//            }
//
//            @Test
//            @DisplayName("(이름이 null일때) It : 400 상태와 검증 메시지를 반환한다")
//            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
//                //given
//                name2 = null;
//                request = new CategoryRequest(name2);
//                doNothing().when(categoryService).updateCategory(categoryId, request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/" + categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andDo(print())
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
//                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."));
//            }
//
//            @Test
//            @DisplayName("(이름이 empty일때) It : 400 상태와 검증 메시지를 반환한다")
//            void it_return_400_bad_request_and_valid_message_if_name_empty() throws Exception {
//                //given
//                name2 = "";
//                request = new CategoryRequest(name2);
//                doNothing().when(categoryService).updateCategory(categoryId, request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/" + categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
//                        .andExpect(jsonPath("$.message").value("name은 필수값입니다."))
//                        .andDo(print());
//            }
//
//            @Test
//            @DisplayName("(이름이 20자를 넘을때) It : 400 상태와 검증 메시지를 반환한다")
//            void it_return_400_bad_request_and_valid_message_if_name_greater_20() throws Exception {
//                //given
//                name2="가나다라마바사아자차카타파하가나다라마바사아자";
//                request = new CategoryRequest(name2);
//                doNothing().when(categoryService).save(request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/" + categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
//                        .andExpect(jsonPath("$.message").value("name은 최대 20자입니다."))
//                        .andDo(print());
//            }
//        }
//        @Nested
//        @DisplayName("Context: thumbnail의 형식이 맞지 않으면")
//        class Context_with_unavailable_thumbnail {
//            @BeforeEach
//            void setUp() {
//                categoryId = 1L;
//                name2 = "거거거거";
//                thumbnail = null;
//                request = new CategoryRequest(name2);
//            }
//
//            @Test
//            @DisplayName("(썸네일이 null일때) It : 400 상태와 검증 메시지를 반환한다")
//            void it_return_400_bad_request_and_valid_message_if_name_null() throws Exception {
//                //given
//                doNothing().when(categoryService).updateCategory(categoryId, request, file);
//
//                //when-then
//                mockMvc.perform(
//                                patch("/api/admin/categories/{categoryId}", categoryId)
//                                        .with(csrf())
//                                        .contentType(MediaType.APPLICATION_JSON)
//                                        .content(om.writeValueAsString(request))
//                        )
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
//                        .andExpect(jsonPath("$.message").value("썸네일은 null이 허용되지 않습니다."))
//                        .andDo(print());
//            }
//        }
//    }
//    @Nested
//    @DisplayName("Discribe: DELETE /{categoryId} 엔드포인트는")
//    class deleteCategory {
//        Long categoryId = 1L;
//
//        @Nested
//        @DisplayName("Context: 올바른 데이터가 주어지면")
//        class Context_with_available_data {
//            @BeforeEach
//            void setUp() {
//            }
//
//            @Test
//            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
//            void it_return_200_ok_and_success_message() throws Exception {
//                //given
//                doNothing().when(categoryService).deleteCategory(categoryId);
//
//                //when-then
//                mockMvc.perform(
//                                delete("/api/admin/categories/{categoryId}", categoryId)
//                                        .with(csrf())
//                        )
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(SuccessCode.CATEGORY_DELETED.name()))
//                        .andExpect(jsonPath("$.message").value(SuccessCode.CATEGORY_DELETED.getSuccessMessage()))
//                        .andDo(print());
//            }
//        }
//        @Nested
//        @DisplayName("Context: 유효하지 않은 id가 주어지면")
//        class Context_with_unavailable_id {
//            @BeforeEach
//            void setUp() {
//            }
//
//            @Test
//            @DisplayName("It : 404 상태와 실패 메시지를 반환한다")
//            void it_return_404_not_found_and_fail_message() throws Exception {
//                //given
//                doThrow(new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
//                        .when(categoryService)
//                        .deleteCategory(categoryId);
//
//                //when-then
//                mockMvc.perform(
//                                delete("/api/admin/categories/{categoryId}", categoryId)
//                                        .with(csrf())
//                        )
//                        .andExpect(status().isNotFound())
//                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                        .andExpect(jsonPath("$.code").value(ErrorCode.CATEGORY_NOT_FOUND.name()))
//                        .andExpect(jsonPath("$.message").value(ErrorCode.CATEGORY_NOT_FOUND.getMessage()))
//                        .andDo(print());
//            }
//        }
//    }

    private AdminCategorySearchResponse searchResponse() {
        return new AdminCategorySearchResponse(
                1L,
                "가방",
                "thumbnail",
                3L,
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 7, 9, 1, 0),
                LocalDateTime.of(2026, 7, 9, 1, 0),
                null
        );
    }

    private AdminCategoryDetailResponse detailResponse() {
        return new AdminCategoryDetailResponse(
                1L,
                "가방",
                "thumbnail",
                3L,
                1L,
                1L,
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 7, 9, 1, 0),
                LocalDateTime.of(2026, 7, 9, 1, 0),
                null
        );
    }
}
