package stitch.crew.hour.product;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    default Product findByIdOrThrow(Long productId){
        return findById(productId).orElseThrow(
                ()->new BusinessException(ErrorCode.USER_DONT_EXISTS)
        );
    }
}
