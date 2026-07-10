package stitch.crew.hour.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.AdminProductSearchResponse;
import stitch.crew.hour.product.dto.ProductSearchResponse;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<ProductSearchResponse> getAllProduct(Pageable pageable, String categoryName);

    Page<AdminProductSearchResponse> getAdminProducts(
            Pageable pageable,
            String keyword,
            String categoryName,
            ProductStatus status,
            Boolean isMain
    );

    List<Product> getMainProducts(Long categoryId);

}
