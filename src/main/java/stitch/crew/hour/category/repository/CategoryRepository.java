package stitch.crew.hour.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByName(String name);

    default Category findByIdOrThrow(Long categoryId){
        return findById(categoryId).orElseThrow(
                ()->new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
        );
    }
}
