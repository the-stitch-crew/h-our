package stitch.crew.hour.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.dto.AdminProductDetailResponse;
import stitch.crew.hour.product.dto.AdminProductSearchResponse;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductCreateResponse;
import stitch.crew.hour.product.dto.ProductStatusUpdateRequest;
import stitch.crew.hour.product.dto.ProductUpdateRequest;
import stitch.crew.hour.user.domain.CurrentUser;

@Tag(name="Product Admin API", description="관리자가 사용하는 상품 관련 API")
 public interface ProductAdminSwaggerSupporter {

    @Operation(
            summary = "관리자 상품 목록 조회",
            description = "관리자가 상품 목록을 검색하고 필터링하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Page<AdminProductSearchResponse>>> getProducts(
            int page,
            int size,
            String keyword,
            String categoryName,
            ProductStatus status,
            Boolean isMain
    );

    @Operation(
            summary = "관리자 상품 상세 조회",
            description = "관리자가 상품 상태와 관계없이 상품 상세를 조회하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<AdminProductDetailResponse>> getProduct(
            Long productId
    );

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

    @Operation(
            summary = "상품 삭제",
            description = "상품을 삭제하는 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 삭제 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
    {
        "success":true,
        "code":"PRODUCT_DELETE_SUCCESS",
        "message":"상품이 정상적으로 삭제되었습니다.",
        "data":null
    }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> deleteProduct(
        CurrentUser currentUser,
        Long productId
    );

    @Operation(
            summary = "상품 수정",
            description = "상품을 수정하는 API",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "productId", description = "상품 ID")
            }
    )
    @RequestBody(
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ProductUpdateRequest.class
                            )
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 수정 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
    {
        "success":true,
        "code":"PRODUCT_UPDATE_SUCCESS",
        "message":"상품이 정상적으로 수정되었습니다.",
        "data":null
    }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> updateProduct(
            CurrentUser currentUser,
            Long productId,
            MultipartFile file,
            ProductUpdateRequest request
    );

    @Operation(
            summary = "상품 상태 변경",
            description = "관리자가 상품 운영 상태를 변경하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> updateStatus(
            Long productId,
            ProductStatusUpdateRequest request
    );

    @Operation(
            summary = "상품 카테고리 메인 상품 설정",
            description = "해당 상품을 카테고리 메인 상품으로 설정하는 API",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "productId", description = "상품 ID")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 수정 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
    {
        "success":true,
        "code":"PRODUCT_DELETE_SUCCESS",
        "message":"상품이 정상적으로 삭제되었습니다.",
        "data":null
    }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> setMainProduct(
            CurrentUser currentUser,
            Long productId
    );

}
