package stitch.crew.hour.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stitch.crew.hour.product.dto.ProductSearchResponse;

public interface ProductRepositoryCustom {

    Page<ProductSearchResponse> getAllProduct(Pageable pageable, String categoryName);

}
