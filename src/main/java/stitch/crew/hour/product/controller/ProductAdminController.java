package stitch.crew.hour.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.dto.AdminProductDetailResponse;
import stitch.crew.hour.product.dto.AdminProductSearchResponse;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductStatusUpdateRequest;
import stitch.crew.hour.product.dto.ProductUpdateRequest;
import stitch.crew.hour.product.service.ProductAdminService;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController implements ProductAdminSwaggerSupporter {

    private final ProductService productService;
    private final ProductAdminService productAdminService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponses<Page<AdminProductSearchResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Boolean isMain
    ) {
        return ApiResult.ok(
                SuccessCode.PRODUCT_READ_SUCCESS,
                productAdminService.getProducts(
                        page,
                        size,
                        keyword,
                        categoryName,
                        status,
                        isMain
                )
        );
    }

    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponses<AdminProductDetailResponse>> getProduct(
            @PathVariable Long productId
    ) {
        return ApiResult.ok(
                SuccessCode.PRODUCT_READ_SUCCESS,
                productAdminService.getProduct(productId)
        );
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponses<ProductCreateResponse>> createProduct(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid ProductCreateRequest request
    ){
        return ApiResult.created(
                SuccessCode.PRODUCT_CREATED_SUCCESS,
                productService.createProduct(
                        currentUser.getId(),
                        request
                )
        );
    }

    @Override
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponses<Void>> deleteProduct(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long productId
    ) {
        productService.deleteProduct(
            currentUser.getId(),
            productId
        );
        return ApiResult.ok(
                SuccessCode.PRODUCT_DELETE_SUCCESS
        );
    }

    @Override
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponses<Void>> updateProduct(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long productId,
            @RequestPart(required = false) MultipartFile file,
            @RequestPart @Valid ProductUpdateRequest request
    ) {
        productService.updateProduct(
                currentUser.getId(),
                productId,
                file,
                request
        );
        return ApiResult.ok(SuccessCode.PRODUCT_UPDATE_SUCCESS);
    }

    @Override
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ApiResponses<Void>> updateStatus(
            @PathVariable Long productId,
            @RequestBody @Valid ProductStatusUpdateRequest request
    ) {
        productAdminService.updateStatus(
                productId,
                request
        );
        return ApiResult.ok(SuccessCode.PRODUCT_UPDATE_SUCCESS);
    }

    @Override
    @PatchMapping("/{productId}/main")
    public ResponseEntity<ApiResponses<Void>> setMainProduct(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long productId
    ) {
        productService.switchToMain(
                currentUser.getId(),
                productId
        );
        return ApiResult.ok(SuccessCode.PRODUCT_UPDATE_SUCCESS);
    }
}
