package stitch.crew.hour.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
                            mediaType = "applicationy/json",
                            schema = @Schema(
                                    implementation = OrderCreateResponse.class
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
