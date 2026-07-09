package stitch.crew.hour.category.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.AdminCategoryDetailResponse;
import stitch.crew.hour.category.dto.AdminCategorySearchResponse;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;

@SpringBootTest
@Transactional
@DisplayName("CategoryAdminService의")
class CategoryAdminServiceTest {

	@Autowired
	CategoryAdminService categoryAdminService;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	EntityManager entityManager;

	@Nested
	@DisplayName("Describe : getCategories()는")
	class Describe_getCategories {

		@Nested
		@DisplayName("Context : 필터 조건이 없는 경우")
		class Context_without_filter {

			@Test
			@DisplayName("It : 카테고리 목록을 최신 생성순으로 조회")
			void It_카테고리_목록을_최신_생성순으로_조회() {
				// given
				Category firstCategory = saveCategory("관리자정렬-첫번째", todayAt(9));
				Category thirdCategory = saveCategory("관리자정렬-세번째", todayAt(10));
				Category secondCategory = saveCategory("관리자정렬-두번째", todayAt(11));

				// when
				Page<AdminCategorySearchResponse> response = categoryAdminService.getCategories(
					0,
					20,
					"관리자정렬-",
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminCategorySearchResponse::categoryId)
					.containsExactly(
						secondCategory.getId(),
						thirdCategory.getId(),
						firstCategory.getId()
					);
			}

			@Test
			@DisplayName("It : 삭제 표시된 카테고리를 제외하고 조회")
			void It_삭제_표시된_카테고리를_제외하고_조회() {
				// given
				Category activeCategory = saveCategory("관리자삭제필터-활성", todayAt(9));
				Category deletedCategory = saveCategory("관리자삭제필터-삭제", todayAt(10));
				deletedCategory.setDeletedAt(todayAt(12));

				// when
				Page<AdminCategorySearchResponse> response = categoryAdminService.getCategories(
					0,
					20,
					"관리자삭제필터-",
					false
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminCategorySearchResponse::categoryId)
					.containsExactly(activeCategory.getId());
			}
		}

		@Nested
		@DisplayName("Context : includeDeleted=true가 주어진 경우")
		class Context_with_include_deleted {

			@Test
			@DisplayName("It : 삭제 표시된 카테고리도 함께 조회")
			void It_삭제_표시된_카테고리도_함께_조회() {
				// given
				Category activeCategory = saveCategory("관리자삭제포함-활성", todayAt(9));
				Category deletedCategory = saveCategory("관리자삭제포함-삭제", todayAt(10));
				deletedCategory.setDeletedAt(todayAt(12));

				// when
				Page<AdminCategorySearchResponse> response = categoryAdminService.getCategories(
					0,
					20,
					"관리자삭제포함-",
					true
				);

				// then
				Assertions.assertThat(response.getContent())
					.extracting(AdminCategorySearchResponse::categoryId)
					.containsExactly(
						deletedCategory.getId(),
						activeCategory.getId()
					);
			}
		}

		@Nested
		@DisplayName("Context : keyword가 주어진 경우")
		class Context_with_keyword {

			@Test
			@DisplayName("It : 카테고리명으로 카테고리를 조회")
			void It_카테고리명으로_카테고리를_조회() {
				// given
				Category bagCategory = saveCategory("관리자가방", todayAt(9));
				saveCategory("관리자신발", todayAt(10));

				// when
				Page<AdminCategorySearchResponse> response = categoryAdminService.getCategories(
					0,
					20,
					"가방",
					false
				);

				// then
				Assertions.assertThat(response.getTotalElements()).isEqualTo(1L);
				Assertions.assertThat(response.getContent().getFirst().categoryId()).isEqualTo(bagCategory.getId());
			}
		}

		@Nested
		@DisplayName("Context : 상품이 포함된 카테고리가 주어진 경우")
		class Context_with_products {

			@Test
			@DisplayName("It : 목록 응답에 상품 수 집계를 포함")
			void It_목록_응답에_상품_수_집계를_포함() {
				// given
				Category category = saveCategory("관리자집계목록", todayAt(9));
				saveProduct("활성 상품", ProductStatus.ACTIVATED, category, false);
				saveProduct("품절 상품", ProductStatus.SOLD_OUT, category, true);
				saveProduct("삭제 상품", ProductStatus.DELETED, category, true);

				// when
				Page<AdminCategorySearchResponse> response = categoryAdminService.getCategories(
					0,
					20,
					"관리자집계목록",
					false
				);

				// then
				AdminCategorySearchResponse categoryResponse = response.getContent().getFirst();
				Assertions.assertThat(categoryResponse.totalProductCount()).isEqualTo(2L);
				Assertions.assertThat(categoryResponse.activeProductCount()).isEqualTo(1L);
				Assertions.assertThat(categoryResponse.soldOutProductCount()).isEqualTo(1L);
				Assertions.assertThat(categoryResponse.mainProductCount()).isEqualTo(1L);
			}
		}
	}

	@Nested
	@DisplayName("Describe : getCategory()는")
	class Describe_getCategory {

		@Nested
		@DisplayName("Context : 존재하는 카테고리 ID가 주어진 경우")
		class Context_with_existing_category_id {

			@Test
			@DisplayName("It : 상세 응답에 상태별 상품 수를 포함")
			void It_상세_응답에_상태별_상품_수를_포함() {
				// given
				Category category = saveCategory("관리자집계상세", todayAt(9));
				saveProduct("활성 상품", ProductStatus.ACTIVATED, category, true);
				saveProduct("품절 상품", ProductStatus.SOLD_OUT, category, false);
				saveProduct("비활성 상품", ProductStatus.DEACTIVATED, category, true);
				saveProduct("삭제 상품", ProductStatus.DELETED, category, true);

				// when
				AdminCategoryDetailResponse response = categoryAdminService.getCategory(category.getId());

				// then
				Assertions.assertThat(response.categoryId()).isEqualTo(category.getId());
				Assertions.assertThat(response.totalProductCount()).isEqualTo(3L);
				Assertions.assertThat(response.activeProductCount()).isEqualTo(1L);
				Assertions.assertThat(response.soldOutProductCount()).isEqualTo(1L);
				Assertions.assertThat(response.deactivatedProductCount()).isEqualTo(1L);
				Assertions.assertThat(response.deletedProductCount()).isEqualTo(1L);
				Assertions.assertThat(response.mainProductCount()).isEqualTo(2L);
			}

			@Test
			@DisplayName("It : 삭제 표시된 카테고리 상세에 deletedAt을 포함")
			void It_삭제_표시된_카테고리_상세에_deletedAt을_포함() {
				// given
				Category category = saveCategory("관리자삭제상세", todayAt(9));
				category.setDeletedAt(todayAt(12));

				// when
				AdminCategoryDetailResponse response = categoryAdminService.getCategory(category.getId());

				// then
				Assertions.assertThat(response.deletedAt()).isNotNull();
			}
		}

		@Nested
		@DisplayName("Context : 존재하지 않는 카테고리 ID가 주어진 경우")
		class Context_with_not_existing_category_id {

			@Test
			@DisplayName("It : CATEGORY_NOT_FOUND 예외가 발생")
			void It_CATEGORY_NOT_FOUND_예외가_발생() {
				// when
				BusinessException exception = assertThrows(
					BusinessException.class,
					() -> categoryAdminService.getCategory(Long.MAX_VALUE)
				);

				// then
				Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
			}
		}
	}

	private Category saveCategory(
		String name,
		LocalDateTime createdAt
	) {
		Category category = categoryRepository.save(new Category(name));
		setCategoryCreatedAt(category, createdAt);
		return category;
	}

	private Product saveProduct(
		String name,
		ProductStatus status,
		Category category,
		Boolean isMain
	) {
		Product product = productRepository.save(
			new Product(
				name,
				2_000L,
				"요약",
				"설명",
				category
			)
		);
		product.switchStatus(status);
		if (isMain) {
			product.setMain();
		}
		return product;
	}

	private void setCategoryCreatedAt(Category category, LocalDateTime createdAt) {
		entityManager.flush();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<Category> update = criteriaBuilder.createCriteriaUpdate(Category.class);
		Root<Category> root = update.from(Category.class);
		Path<LocalDateTime> createdAtPath = root.get("createdAt");
		Path<Long> idPath = root.get("id");

		update.set(createdAtPath, createdAt);
		update.where(criteriaBuilder.equal(idPath, category.getId()));

		entityManager.createQuery(update).executeUpdate();
	}

	private LocalDateTime todayAt(int hour) {
		return LocalDate.now().atTime(hour, 0);
	}
}
