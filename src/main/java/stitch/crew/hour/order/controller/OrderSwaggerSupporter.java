package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.order.dto.OrderCreateFromCartRequest;
import stitch.crew.hour.order.dto.OrderCreateFromProductRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.order.dto.OrderDetailResponse;
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
}
