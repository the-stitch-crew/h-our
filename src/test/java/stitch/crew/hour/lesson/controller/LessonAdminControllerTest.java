package stitch.crew.hour.lesson.controller;

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
import stitch.crew.hour.common.config.JwtAuthenticationFilter;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.lesson.dto.LessonRequest;
import stitch.crew.hour.lesson.service.LessonService;
import stitch.crew.hour.policy.domain.WeekDay;
import stitch.crew.hour.policy.dto.LessonPolicyRequest;
import stitch.crew.hour.policy.service.LessonPolicyService;
import stitch.crew.hour.util.TestUtil;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonAdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private LessonPolicyService policyService;

    @MockitoBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockitoBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;



    Long lessonId = 1L;
    String name = "지갑수업";
    Integer price = 50000;
    Integer duration = 3;



    String email = "newgamer@test.com";
    TestingAuthenticationToken adminAuthentication = TestUtil.createAdminAuthentication(email);

    @Nested
    @DisplayName("Discribe: POST / 엔드포인트는")
    class saveLesson {
        LessonRequest request;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                request = new LessonRequest(name, price, duration);
            }

            @Test
            @DisplayName("It : 201 상태와 성공 메시지를 반환한다")
            void it_return_201_created_and_success_message() throws Exception {
                //given
                doNothing().when(lessonService).saveLesson(request);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_CREATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_CREATED.getSuccessMessage()))
                        .andDo(print());
            }
        }

        @Nested
        @DisplayName("Context: 올바르지 않은 request가 주어지면")
        class Context_with_request_error {
            @BeforeEach
            void setUp() {
            }

            @Test
            @DisplayName("(이름이 null일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_name_not_nullable() throws Exception {
                //given
                request = new LessonRequest(null, price, duration);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("이름은 필수값입니다."))
                        .andDo(print());
            }

            @Test
            @DisplayName("(이름이 비었을때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_name_not_nullable_empty() throws Exception {
                //given
                request = new LessonRequest("", price, duration);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("이름은 필수값입니다."))
                        .andDo(print());
            }

            @Test
            @DisplayName("(가격이 null일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_price_not_nullable() throws Exception {
                //given
                request = new LessonRequest(name, null, duration);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("가격은 필수값입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(가격이 0미만 일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_price_min_0() throws Exception {
                //given
                request = new LessonRequest(name, -10, duration);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("가격은 0원 이상입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(가격이 백만원 초과 일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_price_max_1000000() throws Exception {
                //given
                request = new LessonRequest(name, 5000000, duration);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("가격은 백만원 이하입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(수업시간이 null일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_duration_not_nullable() throws Exception {
                //given
                request = new LessonRequest(name, price, null);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("수업시간은 필수값입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(수업시간이 1 미만일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_duration_min_1() throws Exception {
                //given
                request = new LessonRequest(name, price, 0);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("수업시간은 1시간 이상입니다."))
                        .andDo(print());
            }
            @Test
            @DisplayName("(수업시간이 12 초과일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_duration_max_12() throws Exception {
                //given
                request = new LessonRequest(name, price, 13);

                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("수업시간은 12시간 이하입니다."))
                        .andDo(print());
            }

        }
        @Nested
        @DisplayName("Context: 비즈니스 예외가 발생하면")
        class Context_with_business_error {
            @BeforeEach
            void setUp() {
                request = new LessonRequest(name, price, duration);
            }
            @Test
            @DisplayName("(기존의 이름과 중복일때)It : 409 상태와 이름 중복를 반환한다")
            void it_return_400_badRequest_and_price_min_0() throws Exception {
                //given
                doThrow(new BusinessException(ErrorCode.EXIST_LESSON))
                        .when(lessonService)
                        .saveLesson(eq(request));
                //when-then
                mockMvc.perform(
                                post("/api/admin/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.EXIST_LESSON.name()))
                        .andExpect(jsonPath("$.message").value(ErrorCode.EXIST_LESSON.getMessage()))
                        .andDo(print());
            }
        }

    }
    @Nested
    @DisplayName("Discribe: PUT /{lessonId} 엔드포인트는")
    class updateLesson {
        LessonRequest request;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                request = new LessonRequest(name, price, duration);
            }

            @Test
            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message() throws Exception {
                //given
                doNothing().when(lessonService).updateLesson(lessonId, request);

                //when-then
                mockMvc.perform(
                                put("/api/admin/lessons/" + lessonId)
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_UPDATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_UPDATED.getSuccessMessage()))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: 비즈니스 예외가 발생하면")
        class Context_with_business_error {
            @BeforeEach
            void setUp() {
                String name2 = name;
                request = new LessonRequest(name2, price, duration);
            }
            @Test
            @DisplayName("(id가 유효하지 않을때)It : 404 상태와 해당 없음를 반환한다")
            void it_return_404_notFound_and_no_lesson() throws Exception {
                //given
                doThrow(new BusinessException(ErrorCode.LESSON_NOT_FOUND))
                        .when(lessonService)
                        .updateLesson(eq(lessonId), eq(request));
                //when-then
                mockMvc.perform(
                                put("/api/admin/lessons/" + lessonId)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.LESSON_NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(ErrorCode.LESSON_NOT_FOUND.getMessage()))
                        .andDo(print());
            }
            @Test
            @DisplayName("(기존의 이름과 중복일때)It : 409 상태와 이름 중복를 반환한다")
            void it_return_400_badRequest_and_exist_name() throws Exception {
                //given
                doThrow(new BusinessException(ErrorCode.EXIST_LESSON))
                        .when(lessonService)
                        .updateLesson(eq(lessonId), eq(request));
                //when-then
                mockMvc.perform(
                                put("/api/admin/lessons/" + lessonId)
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isConflict())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.EXIST_LESSON.name()))
                        .andExpect(jsonPath("$.message").value(ErrorCode.EXIST_LESSON.getMessage()))
                        .andDo(print());
            }
        }
    }
    @Nested
    @DisplayName("Discribe: DELETE /{lessonId} 엔드포인트는")
    class deleteCategory {

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
                doNothing().when(lessonService).deleteLesson(lessonId);

                //when-then
                mockMvc.perform(
                                delete("/api/admin/lessons/{lessonId}", lessonId)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_DELETED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_DELETED.getSuccessMessage()))
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
                doThrow(new BusinessException(ErrorCode.LESSON_NOT_FOUND))
                        .when(lessonService)
                        .deleteLesson(lessonId);

                //when-then
                mockMvc.perform(
                                delete("/api/admin/lessons/{lessonId}", lessonId)
                                        .with(csrf())
                        )
                        .andExpect(status().isNotFound())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.LESSON_NOT_FOUND.name()))
                        .andExpect(jsonPath("$.message").value(ErrorCode.LESSON_NOT_FOUND.getMessage()))
                        .andDo(print());
            }
        }
    }
    @Nested
    @DisplayName("Discribe: PUT /{lessonId} 엔드포인트는")
    class updateLessonPolicy {
        Integer reservationAvailableDays = 21;
        Integer reservationAvailableDays2 = 28;
        Integer reservationDeadlineDays = 4;
        Integer cancelDeadlineDays = 1;
        Integer depositAmount = 10000;
        LocalTime startTime = LocalTime.of(9,0);
        LocalTime endTime = LocalTime.of(18,0);
        Set<WeekDay> regularDays = Set.of(WeekDay.SAT, WeekDay.SUN);

        LessonPolicyRequest request;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                request = new LessonPolicyRequest(
                        reservationAvailableDays2,
                        reservationDeadlineDays,
                        cancelDeadlineDays,
                        depositAmount,
                        startTime,
                        endTime,
                        regularDays
                );
            }

            @Test
            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message() throws Exception {
                //given
                doNothing().when(policyService).updateLessonPolicy(request);

                //when-then
                mockMvc.perform(
                                put("/api/admin/lessons/policy")
                                        .with(csrf())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_POLICY_UPDATED.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_POLICY_UPDATED.getSuccessMessage()))
                        .andDo(print());
            }
        }
        @Nested
        @DisplayName("Context: 올바르지 않은 request가 주어지면")
        class Context_with_request_error {
            @BeforeEach
            void setUp() {
            }
            @Test
            @DisplayName("(값중 하나가 null일때)It : 400 상태와 검증 실패 이유를 반환한다")
            void it_return_400_badRequest_and_depositAmount_not_nullable() throws Exception {
                //given
                request = new LessonPolicyRequest(
                        reservationAvailableDays,
                        reservationDeadlineDays,
                        cancelDeadlineDays,
                        null,
                        startTime,
                        endTime,
                        regularDays
                );


                //when-then
                mockMvc.perform(
                                put("/api/admin/lessons/policy")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(om.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_FAILED.name()))
                        .andExpect(jsonPath("$.message").value("예약금액은 필수값입니다."))
                        .andDo(print());
            }
        }
    }
}