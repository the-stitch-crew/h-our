package stitch.crew.hour.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import stitch.crew.hour.category.domain.Category;

public interface CategoryRepositoryCustom {

	Page<Category> getAdminCategories(
		Pageable pageable,
		String keyword,
		Boolean includeDeleted
	);
}
