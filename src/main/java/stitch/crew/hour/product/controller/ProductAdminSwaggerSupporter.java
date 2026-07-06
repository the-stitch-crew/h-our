package stitch.crew.hour.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.user.domain.CurrentUser;

@Tag(name="Product API", description="관리자가 사용하는 상품 관련 API")
 public interface ProductAdminSwaggerSupporter {


    @Operation(
            summary = "상품 생성",
            description = "상품을 생성하는 API"
    )
    @RequestBody(
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProductCreateRequest.class
                            )
                    )
            }
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "주문 생성 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
    {
        "success":true,
        "code":"PRODUCT_CREATED_SUCCESS",
        "message":"상품이 정상적으로 생성되었습니다.",
        "data": {
            "productName":"이정수",
            "price":2000,
            "productId":2
        }
    }
                                            """
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "주문 생성 실패 ( 어드민 아님 )",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
    {
        "success":false,
        "code":"NOT_ADMIN",
        "message":"관리자 권한이 없는 계정입니다.",
        "data":null
    }
                                            """
                                            )
                                    )
                            }
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<ProductCreateResponse>> createProduct(
            CurrentUser currentUser,
            ProductCreateRequest request
    );

}
