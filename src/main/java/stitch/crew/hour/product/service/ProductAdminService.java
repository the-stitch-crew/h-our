package stitch.crew.hour.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.AdminProductDetailResponse;
import stitch.crew.hour.product.dto.AdminProductSearchResponse;
import stitch.crew.hour.product.dto.ProductStatusUpdateRequest;
import stitch.crew.hour.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAdminService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final ProductRepository productRepository;

    public Page<AdminProductSearchResponse> getProducts(
            int page,
            int size,
            String keyword,
            String categoryName,
            ProductStatus status,
            Boolean isMain
    ) {
        return productRepository.getAdminProducts(
                PageRequest.of(page, size, DEFAULT_SORT),
                keyword,
                categoryName,
                status,
                isMain
        );
    }

    public AdminProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findByIdOrThrow(productId);
        return AdminProductDetailResponse.from(product);
    }

    @Transactional
    public void updateStatus(
            Long productId,
            ProductStatusUpdateRequest request
    ) {
        Product product = productRepository.findByIdOrThrow(productId);
        product.switchStatus(request.status());
    }
}
