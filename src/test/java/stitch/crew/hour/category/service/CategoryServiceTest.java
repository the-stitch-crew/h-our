package stitch.crew.hour.category.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategorySaveRequest;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService의")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    String name = "거거거거";
    String thumbnail = "";
    CategorySaveRequest request;
    Category category;

    @Nested
    @DisplayName("Discribe: save 메서드는")
    class Describe_with_save{
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new CategorySaveRequest(name, thumbnail);
                category = new Category(name, thumbnail);
                ReflectionTestUtils.setField(category, "id", 1L);
            }
            @Test
            @DisplayName("It : Category 저장 성공")
            void it_success_category_save() {
                //given
                given(categoryRepository.existsByName(name)).willReturn(false);
                //when
                categoryService.save(request);

                //then
                ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
                verify(categoryRepository).save(captor.capture());
                Category saved = captor.getValue();

                assertThat(saved.getName()).isEqualTo(name);
                assertThat(saved.getThumbnail()).isEqualTo(thumbnail);
            }
        }
        @Nested
        @DisplayName("Context: 이미 존재하는 이름의 데이터가 주어지면")
        class Context_with_existing_name {
            @BeforeEach
            void setup() {
                request = new CategorySaveRequest(name, thumbnail);
                category = new Category(name, thumbnail);
                ReflectionTestUtils.setField(category, "id", 1L);
            }
            @Test
            @DisplayName("It : EXIST_CATEGORY 오류 발생 ")
            void it_throws_exist_category() {
                //given
                given(categoryRepository.existsByName(name)).willReturn(true);
                //when&then
                BusinessException exception = assertThrows(BusinessException.class, () -> categoryService.save(request));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXIST_CATEGORY.getMessage());
            }
        }
    }

}