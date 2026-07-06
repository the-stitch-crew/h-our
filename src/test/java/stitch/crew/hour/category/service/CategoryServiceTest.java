package stitch.crew.hour.category.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategoryResponse;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.image.domain.ThumbnailDomain;
import stitch.crew.hour.image.service.ImageService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService의")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    ImageService imageService;

    String name = "거거거거";
    CategoryRequest request;
    Category category;
    MultipartFile file;
    String thumbnailUrl;

    @Nested
    @DisplayName("Discribe: save 메서드는")
    class Describe_with_save{
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new CategoryRequest(name);
                category = new Category(name);
                ReflectionTestUtils.setField(category, "id", 1L);

            }
            @Test
            @DisplayName("(썸네일 없을때)It : Category 저장 성공")
            void it_success_category_save_without_thumbnail() {
                //given
                given(categoryRepository.existsByName(name)).willReturn(false);
                //when
                categoryService.save(request, file);

                //then
                ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
                verify(categoryRepository).save(captor.capture());
                Category saved = captor.getValue();

                assertThat(saved.getName()).isEqualTo(name);
                assertThat(saved.getThumbnail()).isNull();
            }
            @Test
            @DisplayName("(썸네일 있을때)It : Category 저장 성공")
            void it_success_category_save_with_thumbnail() {
                //given
                file = new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "test".getBytes()
                );
                thumbnailUrl="test/50ef933f-9b39-4f29-bfd8-99098c2fb70c.png";
                given(categoryRepository.existsByName(name)).willReturn(false);
                given(imageService.saveThumbnail(eq(ThumbnailDomain.CATEGORY), any(), eq(file))).willReturn(thumbnailUrl);

                //when
                categoryService.save(request, file);

                //then
                ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
                verify(categoryRepository).save(captor.capture());
                Category saved = captor.getValue();

                assertThat(saved.getName()).isEqualTo(name);
                assertThat(saved.getThumbnail()).isEqualTo(thumbnailUrl);
            }
        }
        @Nested
        @DisplayName("Context: 이미 존재하는 이름의 데이터가 주어지면")
        class Context_with_existing_name {
            @BeforeEach
            void setup() {
                request = new CategoryRequest(name);
                category = new Category(name);
                ReflectionTestUtils.setField(category, "id", 1L);
            }
            @Test
            @DisplayName("It : EXIST_CATEGORY 오류 발생 ")
            void it_throws_exist_category() {
                //given
                given(categoryRepository.existsByName(name)).willReturn(true);
                //when&then
                BusinessException exception = assertThrows(BusinessException.class, () -> categoryService.save(request, file));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXIST_CATEGORY.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Discribe: getCategories 메서드는")
    class Describe_with_getCategories{

        String name2 = "거거거거2";
        Category category2;

        @Nested
        @DisplayName("Context: 기본적으로")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                category = new Category(name);
                category2 = new Category(name2);
                category2.setThumbnail(thumbnailUrl);
            }
            @Test
            @DisplayName("It : Category 목록 조회 성공")
            void it_success_categories_get() {
                //given
                given(categoryRepository.findAll()).willReturn(List.of(category, category2));
                //when
                List<CategoryResponse> response = categoryService.getCategories();

                //then
                Assertions.assertNotNull(response);
                assertThat(response.size()).isEqualTo(2);
                assertThat(response.get(0).name()).isEqualTo(name);
                assertThat(response.get(1).name()).isEqualTo(name2);
                assertThat(response.get(1).thumbnail()).isEqualTo(thumbnailUrl);
            }
        }
    }

    @Nested
    @DisplayName("Discribe: updateCategory 메서드는")
    class Describe_with_updateCategory{
        String name2 = "거거거거2";
        Long categoryId = 1L;
        String thumbnailUrl2;
        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                request = new CategoryRequest(name2);
                category = new Category(name);
                ReflectionTestUtils.setField(category, "id", 1L);
            }
            @Test
            @DisplayName("(파일이 없을때 이름만 변경)It : Category 수정 성공")
            void it_success_category_update_without_thumbnail() {
                //given
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                given(categoryRepository.existsByName(name2)).willReturn(false);
                //when
                categoryService.updateCategory(categoryId, request, file);

                //then
                assertThat(category.getId()).isEqualTo(categoryId);
                assertThat(category.getName()).isEqualTo(name2);
                assertThat(category.getThumbnail()).isNull();
            }
            @Test
            @DisplayName("(파일이 있고 기존 파일이 없을때 썸네일 삭제x)It : Category 수정 성공")
            void it_success_category_update_with_thumbnail_without_existing_thumbnail() {
                //given
                file = new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "test".getBytes()
                );
                thumbnailUrl="test/50ef933f-9b39-4f29-bfd8-99098c2fb70c.png";
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                given(categoryRepository.existsByName(name2)).willReturn(false);
                given(imageService.saveThumbnail(eq(ThumbnailDomain.CATEGORY), any(), eq(file))).willReturn(thumbnailUrl);

                //when
                categoryService.updateCategory(categoryId, request, file);

                //then
                assertThat(category.getId()).isEqualTo(categoryId);
                assertThat(category.getName()).isEqualTo(name2);
                verify(imageService, never()).deleteThumbnail(any());
                verify(imageService).saveThumbnail(
                        ThumbnailDomain.CATEGORY,
                        1L,
                        file
                );
                assertThat(category.getThumbnail()).isEqualTo(thumbnailUrl);
            }
            @Test
            @DisplayName("(파일과 기존 파일 둘다 있을때 썸네일 변경)It : Category 수정 성공")
            void it_success_category_update_with_both_thumbnail() {
                //given
                file = new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "test".getBytes()
                );
                thumbnailUrl="test/50ef933f-9b39-4f29-bfd8-99098c2fb70c.png";
                thumbnailUrl2="test/50ef933f-9b39-4f29-bfd8-99098c2fb70d.png";
                category.setThumbnail(thumbnailUrl);
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                given(categoryRepository.existsByName(name2)).willReturn(false);
                given(imageService.saveThumbnail(eq(ThumbnailDomain.CATEGORY), any(), eq(file))).willReturn(thumbnailUrl2);
                //when
                categoryService.updateCategory(categoryId, request, file);

                //then
                assertThat(category.getId()).isEqualTo(categoryId);
                assertThat(category.getName()).isEqualTo(name2);
                verify(imageService).deleteThumbnail(any());
                verify(imageService).saveThumbnail(
                        ThumbnailDomain.CATEGORY,
                        1L,
                        file
                );
                assertThat(category.getThumbnail()).isEqualTo(thumbnailUrl2);
            }
        }
        @Nested
        @DisplayName("Context: 입력된 id가 유효하지 않는다면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setup() {
                request = new CategoryRequest(name);
                category = new Category(name);
            }
            @Test
            @DisplayName("It : CATEGORY_NOT_FOUND 오류 발생 ")
            void it_throws_not_found_category() {
                //given
                given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());
                //when&then
                BusinessException exception = assertThrows(
                        BusinessException.class, () -> categoryService.updateCategory(categoryId, request, file));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
            }
        }
        @Nested
        @DisplayName("Context: 이미 존재하는 이름의 데이터가 주어지면")
        class Context_with_existing_name {
            @BeforeEach
            void setup() {
                request = new CategoryRequest(name);
                category = new Category(name);
            }
            @Test
            @DisplayName("It : EXIST_CATEGORY 오류 발생 ")
            void it_throws_not_found_category() {
                //given
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                given(categoryRepository.existsByName(name)).willReturn(true);
                //when&then
                BusinessException exception = assertThrows(
                        BusinessException.class, () -> categoryService.updateCategory(categoryId, request, file));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXIST_CATEGORY .getMessage());
            }
        }

    }
    @Nested
    @DisplayName("Discribe: deleteCategory 메서드는")
    class Describe_with_deleteCategory {
        String name = "거거거거";
        Long categoryId = 1L;

        @Nested
        @DisplayName("Context: 올바른 데이터가 주어지면")
        class Context_with_available_data {
            @BeforeEach
            void setup() {
                category = new Category(name);
                ReflectionTestUtils.setField(category, "id", 1L);
            }

            @Test
            @DisplayName("(썸네일 있을때)It : Category 삭제 성공")
            void it_success_category_delete_with_thumbnail() {
                //given
                thumbnailUrl="test/50ef933f-9b39-4f29-bfd8-99098c2fb70c.png";
                category.setThumbnail(thumbnailUrl);
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                //when
                categoryService.deleteCategory(categoryId);

                //then
                verify(categoryRepository).delete(category);
                verify(imageService).deleteThumbnail(any());
            }
            @Test
            @DisplayName("(썸네일 없을때)It : Category 삭제 성공")
            void it_success_category_delete_without_thumbnail() {
                //given
                given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
                //when
                categoryService.deleteCategory(categoryId);

                //then
                verify(categoryRepository).delete(category);
                verify(imageService, never()).deleteThumbnail(any());
            }
        }
        @Nested
        @DisplayName("Context: 잘못된 categoryId가 주어지면")
        class Context_with_unavailable_id {
            @BeforeEach
            void setup() {
            }

            @Test
            @DisplayName("It : CATEGORY_NOT_FOUND 오류 발생")
            void it_throws_category_not_found() {
                //given
                given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());
                //when
                BusinessException exception = assertThrows(
                        BusinessException.class, () -> categoryService.deleteCategory(categoryId));
                assertThat(exception.getMessage()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
            }
        }
    }

}