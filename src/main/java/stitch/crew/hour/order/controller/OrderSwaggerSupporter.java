package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.order.dto.OrderCreateRequest;
import stitch.crew.hour.order.dto.OrderCreateResponse;
import stitch.crew.hour.user.domain.CurrentUser;

public interface OrderSwaggerSupporter {

    @Operation(
            summary = "주문 생성",
            description = "주문을 생성하는 API"
    )
    @RequestBody(
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = OrderCreateRequest.class
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
            OrderCreateRequest request
    );
}
