package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.response.ApiResponses;

import java.util.UUID;

@Tag(name="Order Admin API", description="관리자가 사용하는 주문 관련 API")
public interface OrderAdminSwaggerSupporter {

    @Operation(
            summary = "주문 배송 중 상태로 전환",
            description = "주문 상태를 배송 중 으로 전환하는 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 전환 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"ORDER_IN_DELIVERY",
                "message":"주문이 정상적으로 배송 중 상태로 전환 되었습니다.",
                "data":null
            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> setOrderInDelivery(
            UUID orderNumber
    );

    @Operation(
            summary = "주문 배송 중 상태로 전환",
            description = "주문 상태를 배송 중 으로 전환하는 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 전환 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"ORDER_IN_DELIVERY",
                "message":"주문이 정상적으로 배송 중 상태로 전환 되었습니다.",
                "data":null
            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> setOrderDelivered(
            UUID orderNumber
    );

    @Operation(
            summary = "주문 배송 중 상태로 전환",
            description = "주문 상태를 배송 중 으로 전환하는 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "주문 전환 성공",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
            {
                "success":true,
                "code":"ORDER_IN_DELIVERY",
                "message":"주문이 정상적으로 배송 중 상태로 전환 되었습니다.",
                "data":null
            }
                                            """
                            )
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> setOrderComplete(
            UUID orderNumber
    );
}
