package stitch.crew.hour.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.AdminCategoryDetailResponse;
import stitch.crew.hour.category.dto.AdminCategorySearchResponse;
import stitch.crew.hour.category.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryAdminService {

	private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

	private final CategoryRepository categoryRepository;

	public Page<AdminCategorySearchResponse> getCategories(
		int page,
		int size,
		String keyword,
		Boolean includeDeleted
	) {
		return categoryRepository.getAdminCategories(
				PageRequest.of(page, size, DEFAULT_SORT),
				keyword,
				includeDeleted
			)
			.map(AdminCategorySearchResponse::from);
	}

	public AdminCategoryDetailResponse getCategory(Long categoryId) {
		Category category = categoryRepository.findByIdOrThrow(categoryId);
		return AdminCategoryDetailResponse.from(category);
	}
}
