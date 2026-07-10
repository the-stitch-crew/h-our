package stitch.crew.hour.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.payment.dto.PaymentDetailResponse;
import stitch.crew.hour.product.dto.ProductCreateRequest;
import stitch.crew.hour.user.domain.CurrentUser;

@Tag(name="Payment API", description="모든 사용자가 사용하는 결제 관련 API")
public interface PaymentSwaggerSupporter {

    @Operation(
            summary = "단건 결제 조회",
            description = "결제를 단건 조회하는 API",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "paymentId", description = "결제 ID")
            }
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "단건 조회 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
    {
        "success":true,
        "code":"PAYMENT_READ_SUCCESS",
        "message":"결제 조회에 성공했습니다.",
        "data":{
            "paymentId":1,
            "orderNumber":"0c747c6f-cc8a-474b-aa2b-6ebc65058029",
            "paymentStatus":"COMPLETED",
            "paymentMethod":"EASY_PAY",
            "pgReceiptUrl":null,
            "requestedAt":"2026-07-09T15:27:57.052012900",
            "approvedAt":null
        }
    }
                                            """
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "단건 조회 실패 ( 다른 사용자 )",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
    {
        "success":false,
        "code":"PAYMENT_PERMISSION_DENY",
        "message":"권한이 없어 작업에 실패 했습니다.",
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
    ResponseEntity<ApiResponses<PaymentDetailResponse>> getPaymentDetail(
            CurrentUser currentUser,
            Long paymentId
    );


    @Operation(
            summary = "결제 다건 조회",
            description = "결제를 다건 조회하는 API",
            parameters = {
                    @Parameter(name = "page", description = "조회할 데이터의 페이지"),
                    @Parameter(name = "size", description = "페이지 내 조회할 데이터 수")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "결제 다건 조회 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"PAYMENT_READ_SUCCESS",
                "message":"결제 조회에 성공했습니다.",
                "data":{
                    "content":[
                        {
                            "paymentId":1,
                            "orderNumber":"e740048b-70a9-4bac-8cfa-7a63cd7caf3b",
                            "paymentStatus":"PENDING",
                            "paymentMethod":"EASY_PAY",
                            "pgReceiptUrl":null,
                            "requestedAt":"2026-07-09 15:38:39.452871",
                            "approvedAt":null
                        },
                        {
                            "paymentId":2,
                            "orderNumber":"e740048b-70a9-4bac-8cfa-7a63cd7caf3b",
                            "paymentStatus":"PENDING",
                            "paymentMethod":"EASY_PAY",
                            "pgReceiptUrl":null,
                            "requestedAt":"2026-07-09 15:38:39.457756",
                            "approvedAt":null
                        },
                        {
                            "paymentId":3,
                            "orderNumber":"e740048b-70a9-4bac-8cfa-7a63cd7caf3b",
                            "paymentStatus":"PENDING",
                            "paymentMethod":"EASY_PAY",
                            "pgReceiptUrl":null,
                            "requestedAt":"2026-07-09 15:38:39.460252",
                            "approvedAt":null
                        },
                        {
                            "paymentId":4,
                            "orderNumber":"e740048b-70a9-4bac-8cfa-7a63cd7caf3b",
                            "paymentStatus":"PENDING",
                            "paymentMethod":"EASY_PAY",
                            "pgReceiptUrl":null,
                            "requestedAt":"2026-07-09 15:38:39.462205",
                            "approvedAt":null
                        },
                        {
                            "paymentId":5,
                            "orderNumber":"e740048b-70a9-4bac-8cfa-7a63cd7caf3b",
                            "paymentStatus":"PENDING",
                            "paymentMethod":"EASY_PAY",
                            "pgReceiptUrl":null,
                            "requestedAt":"2026-07-09 15:38:39.46616",
                            "approvedAt":null
                        }
                    ],
                    "empty":false,
                    "first":true,
                    "last":false,
                    "number":0,
                    "numberOfElements":5,
                    "pageable":{
                        "offset":0,
                        "pageNumber":0,
                        "pageSize":5,
                        "paged":true,
                        "sort":{
                            "empty":true,
                            "sorted":false,
                            "unsorted":true
                        },
                        "unpaged":false
                    },
                "size":5,
                "sort":{
                    "empty":true,
                    "sorted":false,
                    "unsorted":true
                },
                "totalElements":10,
                "totalPages":2
                }
            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Page<PaymentDetailResponse>>> getPaymentSearch(
            CurrentUser currentUser,
            Paging paging
    );
}
