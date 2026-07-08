package stitch.crew.hour.policy.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.dto.LessonRequest;
import stitch.crew.hour.lesson.dto.LessonResponse;
import stitch.crew.hour.lesson.repository.LessonRepository;
import stitch.crew.hour.lesson.service.LessonService;
import stitch.crew.hour.policy.domain.LessonPolicy;
import stitch.crew.hour.policy.domain.WeekDay;
import stitch.crew.hour.policy.dto.LessonPolicyRequest;
import stitch.crew.hour.policy.dto.LessonPolicyResponse;
import stitch.crew.hour.policy.repository.LessonPolicyRepository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonPolicyService의")
class LessonPolicyServiceTest {
    @InjectMocks
    private LessonPolicyService policyService;

    @Mock
    LessonPolicyRepository policyRepository;

    Integer reservationAvailableDays = 21;
    Integer reservationDeadlineDays = 4;
    Integer cancelDeadlineDays = 1;
    Integer depositAmount = 10000;
    LocalTime startTime = LocalTime.of(9,0);
    LocalTime endTime = LocalTime.of(18,0);
    Set<WeekDay> regularDays = Set.of(WeekDay.SAT, WeekDay.SUN);



    LessonPolicy policy;
    LessonPolicyRequest request;

    @Nested
    @DisplayName("Describe: getLesson 메서드는")
    class Describe_with_getLesson{

        @Nested
        @DisplayName("Context: 유효한 id라면")
        class Context_with_available_id {
            @BeforeEach
            void setup() {
                policy = new LessonPolicy(reservationAvailableDays, reservationDeadlineDays, cancelDeadlineDays, depositAmount, startTime, endTime, regularDays);
                ReflectionTestUtils.setField(policy, "id", 1L);
            }
            @Test
            @DisplayName("It : 정책 조회 성공")
            void it_success_categories_get() {
                //given
                given(policyRepository.findById(1L)).willReturn(Optional.of(policy));
                //when
                LessonPolicyResponse response = policyService.getLessonPolicy();

                //then
                Assertions.assertNotNull(response);
                assertThat(response.reservationAvailableDays()).isEqualTo(reservationAvailableDays);
                assertThat(response.depositAmount()).isEqualTo(depositAmount);
                assertThat(response.regularDays()).isEqualTo(regularDays);
            }
        }
    }

    @Nested
    @DisplayName("Discribe: updateLesson 메서드는")
    class Describe_with_updateCategory {
        Integer reservationAvailableDays2 = 28;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new LessonPolicyRequest(
                        reservationAvailableDays2,
                        reservationDeadlineDays,
                        cancelDeadlineDays,
                        depositAmount,
                        startTime,
                        endTime,
                        regularDays
                );
                policy = new LessonPolicy(reservationAvailableDays, reservationDeadlineDays, cancelDeadlineDays, depositAmount, startTime, endTime, regularDays);
                ReflectionTestUtils.setField(policy, "id", 1L);
            }

            @Test
            @DisplayName("It : Lesson 수정 성공")
            void it_success_category_update_without_thumbnail() {
                //given
                given(policyRepository.findById(1L)).willReturn(Optional.of(policy));
                //when
                policyService.updateLessonPolicy(request);

                //then
                assertThat(policy.getReservationAvailableDays()).isEqualTo(reservationAvailableDays2);
            }
        }
    }

}