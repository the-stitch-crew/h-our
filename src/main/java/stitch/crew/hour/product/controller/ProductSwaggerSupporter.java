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
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.product.dto.ProductDetailsResponse;
import stitch.crew.hour.product.dto.ProductSearchResponse;
import stitch.crew.hour.user.domain.CurrentUser;

@Tag(name="Product API", description="모든 사용자가 사용하는 상품 관련 API")
public interface ProductSwaggerSupporter {

    @Operation(
            summary = "상품 단건조회",
            description = "상품을 단건 조회하는 API",
            parameters = {
                    @Parameter(name = "productId", description = "상품 ID")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 단건 조회 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
    {
        "success":true,
        "code":"PRODUCT_READ",
        "message":"상품이 정상적으로 조회되었습니다.",
        "data":{
            "productId":1,
            "name":"테스트용 상품",
            "price":2000,
            "thumbnail":null,
            "productStatus":"ACTIVATED",
            "summary":"상품요약",
            "description":"설명글",
            "viewCount":1,
            "salesCount":0,
            "categoryName":"카테고리명"
        }
    }
                                            """
                            )
                    )
            }
    )
    ResponseEntity<ApiResponses<ProductDetailsResponse>> getProduct(
            Long productId
    );

    @Operation(
            summary = "상품 다건조회",
            description = "상품을 다건 조회하는 API",
            parameters = {
                    @Parameter(name = "categoryName", description = "카테고리명"),
                    @Parameter(name = "page", description = "조회할 데이터의 페이지"),
                    @Parameter(name = "size", description = "페이지 내 조회할 데이터 수")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 다건 조회 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
    {
        "success":true,
        "code":"PRODUCT_READ",
        "message":"상품이 정상적으로 조회되었습니다.",
        "data":{
            "content":[
                {
                    "productId":2,
                    "name":"테스트용 상품",
                    "price":2000,
                    "thumbnail":null,
                    "productStatus":"ACTIVATED",
                    "summary":"상품요약"
                }
            ],
            "empty":false,
            "first":true,
            "last":true,
            "number":0,
            "numberOfElements":1,
            "pageable":{
                "offset":0,
                "pageNumber":0,
                "pageSize":20,
                "paged":true,
                "sort":{
                    "empty":true,
                    "sorted":false,
                    "unsorted":true
                },
                "unpaged":false
            },
            "size":20,
            "sort":{
                "empty":true,
                "sorted":false,
                "unsorted":true
            },
            "totalElements":1,
            "totalPages":1
        }
    }
                                            """
                            )
                    )
            }
    )
    ResponseEntity<ApiResponses<Page<ProductSearchResponse>>> getProducts(
            Paging paging,
            String categoryName
    );

}
