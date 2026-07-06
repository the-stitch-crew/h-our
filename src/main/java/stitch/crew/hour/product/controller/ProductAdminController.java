package stitch.crew.hour.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
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

}
