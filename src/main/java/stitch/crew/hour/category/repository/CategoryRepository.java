package stitch.crew.hour.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {
    Boolean existsByName(String name);

    Optional<Category> findByName(String categoryName);

    default Category findByIdOrThrow(Long categoryId){
        return findById(categoryId).orElseThrow(
                ()->new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
        );
    }

    default Category findByNameOrThrow(String categoryName){
        return findByName(categoryName).orElseThrow(
                ()->new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
        );
    }
}
