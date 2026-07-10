package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.dto.AdminOrderDetailResponse;
import stitch.crew.hour.order.dto.AdminOrderSearchResponse;

import java.util.UUID;

@Tag(name="Order Admin API", description="관리자가 사용하는 주문 관련 API")
public interface OrderAdminSwaggerSupporter {

    @Operation(
            summary = "관리자 주문 목록 조회",
            description = "관리자가 전체 주문 목록을 최신순으로 조회하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Page<AdminOrderSearchResponse>>> getOrders(
            int page,
            int size,
            OrderStatus status
    );

    @Operation(
            summary = "관리자 주문 상세 조회",
            description = "관리자가 주문 상세 정보와 주문 상품 목록을 조회하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<AdminOrderDetailResponse>> getOrder(
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

    @Operation(
            summary = "주문 취소 상태로 전환",
            description = "주문 상태를 취소로 전환하는 API"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<Void>> cancelOrder(
            UUID orderNumber
    );
}
