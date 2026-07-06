package stitch.crew.hour.product.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductUpdateRequest;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController implements ProductAdminSwaggerSupporter {

    private final ProductService productService;

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
           @RequestBody @Valid ProductUpdateRequest request
    ) {
        productService.updateProduct(
                currentUser.getId(),
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
