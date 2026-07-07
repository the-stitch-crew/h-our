package stitch.crew.hour.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductSearchResponse;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<ProductSearchResponse> getAllProduct(Pageable pageable, String categoryName);

    List<Product> getMainProducts(Long categoryId);

}
