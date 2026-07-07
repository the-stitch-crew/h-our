package stitch.crew.hour.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductDetailsResponse;
import stitch.crew.hour.product.dto.ProductSearchResponse;
import stitch.crew.hour.product.service.ProductService;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController implements ProductSwaggerSupporter {

    private final ProductService productService;


    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponses<ProductDetailsResponse>> getProduct(
            @PathVariable(required = true) Long productId
    ) {
        return ApiResult.ok(
            SuccessCode.PRODUCT_READ,
            productService.getProductDetail(productId)
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponses<Page<ProductSearchResponse>>> getProducts(
            Paging paging,
            @RequestParam(required = false, defaultValue = "") String categoryName
    ) {
        return ApiResult.ok(
            SuccessCode.PRODUCT_READ,
                productService.getProductSearch(
                    paging.toPageable(),
                    categoryName
            )
        );
    }
}
