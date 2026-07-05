package stitch.crew.hour.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    default Product findByIdOrThrow(Long productId){
        return findById(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
    }
}
