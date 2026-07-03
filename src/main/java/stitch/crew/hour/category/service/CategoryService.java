package stitch.crew.hour.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategorySaveRequest;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public void save(CategorySaveRequest request) {
        PreConditions.validate(!categoryRepository.existsByName(request.name()), ErrorCode.EXIST_CATEGORY);
        Category category = new Category(request.name(), request.thumbnail());
        categoryRepository.save(category);
    }
}
