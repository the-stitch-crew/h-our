package stitch.crew.hour.category.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategoryResponse;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(CategoryRequest request) {
        PreConditions.validate(!categoryRepository.existsByName(request.name()), ErrorCode.EXIST_CATEGORY);
        Category category = new Category(request.name(), request.thumbnail());
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryResponse::from).toList();
    }

    @Transactional
    public void updateCategory(Long categoryId, @Valid CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        PreConditions.validate(!categoryRepository.existsByName(request.name()), ErrorCode.EXIST_CATEGORY);
        category.update(request.name(), request.thumbnail());
    }
}
