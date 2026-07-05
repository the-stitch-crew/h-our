package stitch.crew.hour.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.category.domain.Category;
import stitch.crew.hour.category.repository.CategoryRepository;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductDetailsResponse;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.constant.UserRole;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @PreAuthorize("isAuthenticated()")
    public ProductCreateResponse createProduct(
            Long userId,
            Long categoryId,
            ProductCreateRequest request
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        Category foundedCategory = categoryRepository.findByIdOrThrow(categoryId);

        PreConditions.validate(
                foundedUser.getRole().equals(UserRole.ADMIN),
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

    @PreAuthorize("isAuthenticated()")
    public ProductDetailsResponse getProductDetail(
        Long productId
    ){
        return ProductDetailsResponse.from(
            productRepository.findByIdOrThrow(productId)
        );
    }

}
