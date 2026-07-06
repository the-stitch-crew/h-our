package stitch.crew.hour.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.dto.CategoryResponse;
import stitch.crew.hour.category.dto.CategoryRequest;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.image.domain.ThumbnailDomain;
import stitch.crew.hour.image.service.ImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Transactional
    public void save(CategoryRequest request, MultipartFile file) {
        PreConditions.check(categoryRepository.existsByName(request.name()), ErrorCode.EXIST_CATEGORY);
        Category category = new Category(request.name());
        categoryRepository.save(category);
        if (file != null) {
            String thumbnail = imageService.saveThumbnail(ThumbnailDomain.CATEGORY, category.getId(),  file);
            category.setThumbnail(thumbnail);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(c ->
                CategoryResponse.from(c, c.getThumbnail()==null?null:imageService.getPresignedUrl(c.getThumbnail())))
                .toList();
    }

    @Transactional
    public void updateCategory(Long categoryId, CategoryRequest request, MultipartFile file) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        PreConditions.check(categoryRepository.existsByName(request.name()), ErrorCode.EXIST_CATEGORY);
        if (file==null) {
            category.setName(request.name());
        } else {
            if (category.getThumbnail()!=null) imageService.deleteThumbnail(category.getThumbnail());
            String newThumbnail = imageService.saveThumbnail(ThumbnailDomain.CATEGORY, categoryId, file);
            category.update(request.name(), newThumbnail);
        }
    }


    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
        if (category.getThumbnail()!=null) imageService.deleteThumbnail(category.getThumbnail());
    }
}
