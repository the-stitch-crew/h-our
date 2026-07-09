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
import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.lesson.service.LessonService;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;
import stitch.crew.hour.policy.service.LessonPolicyService;
import stitch.crew.hour.util.TestUtil;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonControllerTest {
    @Autowired
    MockMvc mockMvc;

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

    Long lessonId2 = 2L;
    String name2 = "키링수업";
    Integer price2 = 30000;
    Integer duration2 = 2;

    LessonResponse response1 = new LessonResponse(lessonId, name, price, duration);
    LessonResponse response2 = new LessonResponse(lessonId2, name2, price2, duration2);

    String email = "newgamer@test.com";
    TestingAuthenticationToken adminAuthentication = TestUtil.createAdminAuthentication(email);

    @Nested
    @DisplayName("Discribe: GET / 엔드포인트는")
    class getCategories {
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {

            }
            @Test
            @DisplayName("It : 200 상태와 성공 메시지, 데이터를 반환한다")
            void it_return_200_ok_and_success_message_and_data() throws Exception {
                //given
                given(lessonService.getLessons()).willReturn(Arrays.asList(response1, response2));

                //when-then
                mockMvc.perform(
                                get("/api/lessons")
                                        .with(csrf())
                                        .principal(adminAuthentication)
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_READ.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_READ.getSuccessMessage()))
                        .andExpect(jsonPath("$.data.size()").value(2))
                        .andExpect(jsonPath("$.data[0].name").value(name))
                        .andExpect(jsonPath("$.data[0].price").value(price))
                        .andExpect(jsonPath("$.data[0].duration").value(duration))
                        .andExpect(jsonPath("$.data[1].name").value(name2))
                        .andExpect(jsonPath("$.data[1].price").value(price2))
                        .andExpect(jsonPath("$.data[1].duration").value(duration2))
                        .andDo(print());
            }

        }
    }

    @Nested
    @DisplayName("Discribe: GET /{lessonId} 엔드포인트는")
    class getCategory {

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
                given(lessonService.getLesson(lessonId)).willReturn(response1);

                //when-then
                mockMvc.perform(
                                get("/api/lessons/{lessonId}", lessonId)
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_READ.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_READ.getSuccessMessage()))
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
                        .getLesson(lessonId);

                //when-then
                mockMvc.perform(
                                get("/api/lessons/{lessonId}", lessonId)
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
    @DisplayName("Discribe: GET /policy 엔드포인트는")
    class getLessonPolicy {

        Integer reservationAvailableDays = 21;
        Integer reservationDeadlineDays = 4;
        Integer cancelDeadlineDays = 1;
        Integer depositAmount = 10000;
        LocalTime startTime = LocalTime.of(9,0);
        LocalTime endTime = LocalTime.of(18,0);
        Set<DayOfWeek> closedDays = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        LessonPolicyResponse response;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setUp() {
                response = new LessonPolicyResponse(
                        reservationAvailableDays,
                        reservationDeadlineDays,
                        cancelDeadlineDays,
                        depositAmount,
                        startTime,
                        endTime,
                        closedDays
                );
            }

            @Test
            @DisplayName("It : 200 상태와 성공 메시지를 반환한다")
            void it_return_200_ok_and_success_message() throws Exception {
                //given
                given(policyService.getLessonPolicy()).willReturn(response);

                //when-then
                mockMvc.perform(
                                get("/api/lessons/policy")
                                        .with(csrf())
                        )
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code").value(SuccessCode.LESSON_POLICY_READ.name()))
                        .andExpect(jsonPath("$.message").value(SuccessCode.LESSON_POLICY_READ.getSuccessMessage()))
                        .andDo(print());
            }
        }
    }
}


