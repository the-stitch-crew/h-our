package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.dto.Paging;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.order.dto.*;
import stitch.crew.hour.user.domain.CurrentUser;

import java.util.UUID;

@Tag(name="Order API", description="모든 사용자가 사용하는 주문 관련 API")
public interface OrderSwaggerSupporter {

    @Operation(
            summary = "주문 생성",
            description = "단건 상품에 대한 주문을 생성하는 API"
    )
    @RequestBody(
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = OrderCreateFromProductRequest.class
                            )
                    )
            }
    )
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
                                "code":"ORDER_CREATED_SUCCESS",
                                "message":"주문이 정상적으로 생성되었습니다.",
                                "data" : {
                                    "orderNumber":"23fb949e-c48b-4a5e-9e2f-5b54b34eb089",
                                    "orderProducts": [
                                        {   
                                            "name":"상품명1",
                                            "amount":1,
                                            "price":2000,
                                            "productId":1
                                        },
                                        {
                                            "name":"상품명1",
                                            "amount":1,
                                            "price":2000,
                                            "productId":2}
                                    ],
                                    "totalPrice":7500,
                                    "deliveryFee":3500,
                                    "address":"원주시",
                                    "postalCode":"26421312",
                                    "receiverName":"이정수",
                                    "receiverPhoneNumber":"01041245512",
                                    "phoneNumber":"0107615022313619",
                                    "ordererName":"이름",
                                    "orderStatus":"ORDERED",
                                    "createdAt":"2026-07-05T12:03:06.5673656"
                                }
                            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<OrderCreateResponse>> createOrder(
            CurrentUser currentUser,
            OrderCreateFromProductRequest request
    );

    @Operation(
            summary = "주문 생성 ( 장바구니 )",
            description = "장바구니로 부터 주문을 생성하는 API"
    )
    @RequestBody(
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = OrderCreateFromProductRequest.class
                            )
                    )
            }
    )
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
                                "code":"ORDER_CREATED_SUCCESS",
                                "message":"주문이 정상적으로 생성되었습니다.",
                                "data":{
                                    "orderNumber":"47f23a3c-8590-44d4-b427-6848b5dd9829",
                                    "orderProducts":[
                                        {
                                            "name":"테스트용 상품",
                                            "amount":2,
                                            "price":2000,
                                            "productId":1
                                        }
                                    ],
                                    "totalPrice":7500,
                                    "deliveryFee":3500,
                                    "address":"원주시",
                                    "postalCode":"26421312",
                                    "receiverName":"이정수",
                                    "receiverPhoneNumber":"01041245512",
                                    "phoneNumber":"010",
                                    "ordererName":"이름",
                                    "orderStatus":"ORDERED",
                                    "createdAt":"2026-07-07T15:15:38.8604633"
                                }
                            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<OrderCreateResponse>> createOrderFromCart(
            CurrentUser currentUser,
            OrderCreateFromCartRequest request
    );


    @Operation(
            summary = "주문 조회",
            description = "주문을 조회하는 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 조회 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"PRODUCT_READ_SUCCESS",
                "message":"상품이 정상적으로 조회되었습니다.",
                "data":{
                    "orderNumber":"027e4e43-10ad-456f-b41c-6c97b7b7b9ec",
                    "totalPrice":7500,
                    "orderStatus":"ORDERED",
                    "deliveryFee":3500,
                    "address":"원주시",
                    "postalCode":"26421312",
                    "receiverName":"이름",
                    "receiverPhoneNumber":
                    "01041245512",
                    "orderProducts":[
                        {
                            "name":"테스트용 상품",
                            "amount":2,
                            "price":2000,
                            "productId":1
                        }
                    ]
                }
            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<OrderDetailResponse>> getOrderDetail(
            CurrentUser currentUser,
            UUID orderNumber
    );

    @Operation(
            summary = "주문 다건 조회",
            description = "주문을 다건 조회하는 API",
            parameters = {
                    @Parameter(name = "page", description = "조회할 데이터의 페이지"),
                    @Parameter(name = "size", description = "페이지 내 조회할 데이터 수")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 조회 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"ORDER_READ_SUCCESS",
                "message":"주문이 정상적으로 조회되었습니다.",
                "data":{
                    "content":[
                        {
                            "orderNumber":"db140232-1273-448c-9134-619fd550dd3b",
                            "totalPrice":7500,
                            "orderStatus":"ORDERED"
                        },
                        {   
                            "orderNumber":"e44e1c4b-e28e-4d62-8bef-c33dee212901",
                            "totalPrice":7500,
                            "orderStatus":"ORDERED"
                        },
                        {
                            "orderNumber":"63fe9d29-1382-46e4-87e6-d25c06d3c2a7",
                            "totalPrice":7500,
                            "orderStatus":"ORDERED"
                        },
                        {
                            "orderNumber":"a932dbb9-3ace-40fb-93f7-87539112441a",
                            "totalPrice":7500,
                            "orderStatus":"ORDERED"
                        },
                        {
                            "orderNumber":"5eaa01dc-3c56-42ae-b026-0d2ba61691ff",
                            "totalPrice":7500,
                            "orderStatus":"ORDERED"
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
    ResponseEntity<ApiResponses<Page<OrderSearchResponse>>> getOrderSearches(
            CurrentUser currentUser,
            Paging paging
    );
}
