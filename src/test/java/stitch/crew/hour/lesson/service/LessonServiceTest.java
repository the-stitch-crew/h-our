package stitch.crew.hour.lesson.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonService의")
class LessonServiceTest {

    @InjectMocks
    private LessonService lessonService;

    @Mock
    private LessonRepository lessonRepository;

    Long lessonId = 1L;
    String name = "지갑수업";
    Integer price = 50000;
    Integer duration = 3;

    LessonRequest request;
    Lesson lesson;

    @Nested
    @DisplayName("Describe: save 메서드는")
    class Describe_with_save{
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new LessonRequest(name, price, duration);
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", lessonId);
            }
            @Test
            @DisplayName("It : Lesson 저장 성공")
            void it_success_category_save_without_thumbnail() {
                //given
                given(lessonRepository.existsByName(name)).willReturn(false);
                //when
                lessonService.saveLesson(request);

                //then
                ArgumentCaptor<Lesson> captor = ArgumentCaptor.forClass(Lesson.class);
                verify(lessonRepository).save(captor.capture());
                Lesson saved = captor.getValue();

                assertThat(saved.getName()).isEqualTo(name);
                assertThat(saved.getPrice()).isEqualTo(price);
                assertThat(saved.getDuration()).isEqualTo(duration);
            }
        }
        @Nested
        @DisplayName("Context: 이미 존재하는 이름의 데이터가 주어지면")
        class Context_with_existing_name {
            @BeforeEach
            void setup() {
                request = new LessonRequest(name, price, duration);
                lesson = new Lesson(name, price, duration);
            }
            @Test
            @DisplayName("It : EXIST_CATEGORY 오류 발생 ")
            void it_throws_exist_category() {
                //given
                given(lessonRepository.existsByName(name)).willReturn(true);
                //when&then
                BusinessException exception = assertThrows(BusinessException.class, () -> lessonService.saveLesson(request));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXIST_LESSON.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Describe: getLessons 메서드는")
    class Describe_with_getCategories{

        Long lessonId2 = 2L;
        String name2 = "키링수업";
        Integer price2 = 30000;
        Integer duration2 = 2;
        Lesson lesson2;

        @Nested
        @DisplayName("Context: 기본적으로")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                lesson = new Lesson(name, price, duration);
                lesson2 = new Lesson(name2, price2, duration2);
                ReflectionTestUtils.setField(lesson, "id", lessonId);
                ReflectionTestUtils.setField(lesson2, "id", lessonId2);
            }
            @Test
            @DisplayName("It : Category 목록 조회 성공")
            void it_success_categories_get() {
                //given
                given(lessonRepository.findAll()).willReturn(List.of(lesson, lesson2));
                //when
                List<LessonResponse> response = lessonService.getLessons();

                //then
                Assertions.assertNotNull(response);
                assertThat(response.size()).isEqualTo(2);
                assertThat(response.get(0).name()).isEqualTo(name);
                assertThat(response.get(0).price()).isEqualTo(price);
                assertThat(response.get(0).duration()).isEqualTo(duration);
                assertThat(response.get(1).name()).isEqualTo(name2);
                assertThat(response.get(1).price()).isEqualTo(price2);
                assertThat(response.get(1).duration()).isEqualTo(duration2);
            }
        }
    }

    @Nested
    @DisplayName("Describe: getLesson 메서드는")
    class Describe_with_getLesson{

        @Nested
        @DisplayName("Context: 유효한 id라면")
        class Context_with_available_id {
            @BeforeEach
            void setup() {
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", 1L);
            }
            @Test
            @DisplayName("It : lesson 조회 성공")
            void it_success_categories_get() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.of(lesson));
                //when
                LessonResponse response = lessonService.getLesson(lessonId);

                //then
                Assertions.assertNotNull(response);
                assertThat(response.name()).isEqualTo(name);
                assertThat(response.price()).isEqualTo(price);
                assertThat(response.duration()).isEqualTo(duration);
            }
        }
        @Nested
        @DisplayName("Context: 유효하지 않은 id라면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setup() {
            }
            @Test
            @DisplayName("It : LESSON_NOT_FOUND 오류 발생")
            void it_throws_lesson_not_found() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.empty());
                //when&then
                BusinessException exception = assertThrows(BusinessException.class, () -> lessonService.getLesson(lessonId));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.LESSON_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Discribe: updateLesson 메서드는")
    class Describe_with_updateCategory{


        String name2 = "키링수업";
        Integer price2 = 30000;
        Integer duration2 = 2;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new LessonRequest(name2, price2, duration2);
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", lessonId);
            }

            @Test
            @DisplayName("It : Lesson 수정 성공")
            void it_success_category_update_without_thumbnail() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.of(lesson));
                //when
                lessonService.updateLesson(lessonId, request);

                //then
                assertThat(lesson.getId()).isEqualTo(lessonId);
                assertThat(lesson.getName()).isEqualTo(name2);
                assertThat(lesson.getPrice()).isEqualTo(price2);
                assertThat(lesson.getDuration()).isEqualTo(duration2);
            }
        }
        @Nested
        @DisplayName("Context: 입력된 id가 유효하지 않는다면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setup() {
                request = new LessonRequest(name, price, duration);
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", lessonId);
            }
            @Test
            @DisplayName("It : LESSON_NOT_FOUND 오류 발생 ")
            void it_throws_not_found_category() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.empty());
                //when&then
                BusinessException exception = assertThrows(
                        BusinessException.class, () -> lessonService.updateLesson(lessonId, request));
            assertThat(exception.getMessage()).isEqualTo(ErrorCode.LESSON_NOT_FOUND.getMessage());
            }
        }
        @Nested
        @DisplayName("Context: 이름을 수정하는데 이미 존재하는 이름의 데이터가 주어지면")
        class Context_with_existing_name {
            @BeforeEach
            void setup() {
                request = new LessonRequest(name2, price, duration);
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", lessonId);
            }
            @Test
            @DisplayName("It : EXIST_LESSON 오류 발생 ")
            void it_throws_not_found_lesson() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.of(lesson));
                given(lessonRepository.existsByName(name2)).willReturn(true);

                //when&then
                BusinessException exception = assertThrows(
                        BusinessException.class, () -> lessonService.updateLesson(lessonId, request));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXIST_LESSON .getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Describe: deleteLesson 메서드는")
    class Describe_with_deleteLesson{

        @Nested
        @DisplayName("Context: 유효한 id라면")
        class Context_with_available_id {
            @BeforeEach
            void setup() {
                lesson = new Lesson(name, price, duration);
                ReflectionTestUtils.setField(lesson, "id", 1L);
            }
            @Test
            @DisplayName("It : Lesson 삭제 성공")
            void it_success_categories_get() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.of(lesson));
                //when
                lessonService.deleteLesson(lessonId);

                //then
                verify(lessonRepository).delete(lesson);
            }
        }
        @Nested
        @DisplayName("Context: 유효하지 않은 id라면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setup() {
            }
            @Test
            @DisplayName("It : LESSON_NOT_FOUND 오류 발생")
            void it_throws_lesson_not_found() {
                //given
                given(lessonRepository.findById(lessonId)).willReturn(Optional.empty());
                //when&then
                BusinessException exception = assertThrows(BusinessException.class, () -> lessonService.getLesson(lessonId));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.LESSON_NOT_FOUND.getMessage());
            }
        }
    }


}