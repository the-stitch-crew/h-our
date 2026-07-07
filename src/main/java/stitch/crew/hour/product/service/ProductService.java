package stitch.crew.hour.product.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.*;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.util.List;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ProductCreateResponse createProduct(
            Long userId,
            ProductCreateRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        Category foundedCategory = categoryRepository.findByIdOrThrow(request.categoryId());

        PreConditions.validate(
                foundedUser.getRole().equals(Role.ADMIN),
                ErrorCode.NOT_ADMIN
        );

        return ProductCreateResponse.from(
                productRepository.save(
                        new Product(
                                request.name(),
                                request.price(),
                                request.summary(),
                                request.description(),
                                foundedCategory
                        )
                )
        );
    }

    public ProductDetailsResponse getProductDetail(
        Long productId
    ){
        Product founded = productRepository.findByIdOrThrow(productId);
        founded.increaseViewCount();
        return ProductDetailsResponse.from(founded
        );
    }

    public Page<ProductSearchResponse> getProductSearch(
        PageRequest pageRequest,
        String categoryName
    ){
        return productRepository.getAllProduct(
                pageRequest,
                categoryName
        );
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteProduct(
            Long userId,
            Long productId
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        PreConditions.validate(
                foundedUser.getRole().equals(Role.ADMIN),
                ErrorCode.NOT_ADMIN
        );

        Product foundedProduct = productRepository.findByIdOrThrow(productId);
        foundedProduct.switchStatus(ProductStatus.DELETED);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateProduct(
        Long userId,
        Long productId,
        ProductUpdateRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        PreConditions.validate(
                foundedUser.getRole().equals(Role.ADMIN),
                ErrorCode.NOT_ADMIN
        );

        Product foundedProduct = productRepository.findByIdOrThrow(productId);

        if (Strings.isNotBlank(request.name())) foundedProduct.setName(request.name());
        if (request.price() != null) foundedProduct.setPrice(request.price());
        if (Strings.isNotBlank(request.thumbnail())) foundedProduct.setThumbnail(request.thumbnail());
        if (Strings.isNotBlank(request.summary())) foundedProduct.setSummary(request.summary());
        if (Strings.isNotBlank(request.description())) foundedProduct.setDescription(request.description());
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void switchToMain(
            Long userId,
            Long productId
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        PreConditions.validate(
                foundedUser.getRole().equals(Role.ADMIN),
                ErrorCode.NOT_ADMIN
        );

        Product targetProduct = productRepository.findByIdOrThrow(productId);

        Category targetCategory = targetProduct.getCategory();

        List<Product> mainProducts = productRepository.getMainProducts(targetCategory.getId());

        PreConditions.validate(
                !mainProducts.contains(targetProduct),
                ErrorCode.PRODUCT_ALREADY_MAIN
        );

        if( mainProducts.size() < 10 ) {
            targetProduct.setMain();
        }
        else {
            Product product = mainProducts.stream().sorted((s1,s2) ->
                s1.getLastErolledToMain().compareTo(s2.getLastErolledToMain())
            ).findFirst().orElseThrow(
                    ()-> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
            );
            product.unsetMain();
            targetProduct.setMain();
        }
    }
}
