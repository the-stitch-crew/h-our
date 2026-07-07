package stitch.crew.hour.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import stitch.crew.hour.cart.dto.AddCartProductRequest;
import stitch.crew.hour.cart.dto.CartDetailResponse;
import stitch.crew.hour.cart.dto.UpdateCartProductRequest;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.user.domain.CurrentUser;

@Tag(name="Cart API", description="모든 사용자가 사용하는 장바구니 관련 API")
public interface CartSwaggerSupporter {

    @Operation(
            summary = "장바구니 조회",
            description = "장바구니를 조회하는 API"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 조회 성공",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            value = """
                    {
                        "success":true,
                        "code":"CART_RETRIEVE_SUCCESS",
                        "message":"장바구니 조회에 성공했습니다.",
                        "data":{
                            "cartId":1,
                            "userId":1,
                            "products":[
                                {
                                    "cartProductId":1,
                                    "amount":1,
                                    "productName":"테스트용 상품",
                                    "price":2000,
                                    "totalPrice":2000
                                }
                            ],
                            "totalPrice":2000
                        }
                    }
                                            """
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "장바구니 조회 실패",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            value = """
                    {
                        "success":false,
                        "code":"NO_CART",
                        "message":"장바구니가 없는 계정입니다.",
                        "data":null
                    }
                                            """
                                    )
                            )
                    }
            )
    })

    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<CartDetailResponse>> getCart(CurrentUser currentUser);


    @Operation(
            summary = "장바구니 생성",
            description = "장바구니를 생성하는 API"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "장바구니 생성 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
                    {
                        "success":true,
                        "code":"CART_CREATED_SUCCESS",
                        "message":"장바구니가 정상적으로 생성되었습니다.",
                        "data":{
                            "cartId":2,
                            "userId":2,
                            "products":[],
                            "totalPrice":0
                        }
                    }
                                            """
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이미 장바구니 존재하는 계정에는 장바구니 생성 실패",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
                    {
                        "success":false,
                        "code":"CART_ALREADY_EXISTS",
                        "message":"이미 장바구니가 존재하는 계정입니다.",
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
    ResponseEntity<ApiResponses<CartDetailResponse>> createCart(CurrentUser currentUser);

    @Operation(
            summary = "장바구니 상품 추가",
            description = "장바구니에 상품을 추가하는 API"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니 상품 추가 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
                    {
                        "success":true,
                        "code":"CART_ADDITION_SUCCESS",
                        "message":"장바구니에 상품이 정상적으로 추가되었습니다.",
                        "data":{
                            "cartId":1,
                            "userId":1,
                            "products":[
                                {
                                    "cartProductId":1,
                                    "amount":1,
                                    "productName":"테스트용 상품",
                                    "price":2000,
                                    "totalPrice":2000
                                },
                                {
                                    "cartProductId":2,
                                    "amount":2,
                                    "productName":"테스트용 상품2",
                                    "price":3000,
                                    "totalPrice":6000
                                }
                            ],
                            "totalPrice":8000
                        }
                    }
                                            """
                                            )
                                    )
                            }
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<ApiResponses<CartDetailResponse>> addProductToCart(
            CurrentUser currentUser,
            Long cartId,
            AddCartProductRequest request
    );

    @Operation(
            summary = "장바구니 상품 수정",
            description = "장바구니의 상품을 수정하는 API"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니 상품 수정 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
                    {
                        "success":true,
                        "code":"CART_UPDATE_SUCCESS",
                        "message":"장바구니의 상품이 정상적으로 변경되었습니다.",
                        "data":{
                            "cartId":1,
                            "userId":1,
                            "products":[
                                {
                                    "cartProductId":1,
                                    "amount":50,
                                    "productName":"테스트용 상품",
                                    "price":2000,
                                    "totalPrice":100000
                                }
                            ],
                            "totalPrice":100000
                        }
                    }
                                            """
                                            )
                                    )
                            }
                    )
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponses<CartDetailResponse>> updateCartProduct(
            CurrentUser currentUser,
            Long cartId,
            UpdateCartProductRequest request
    );

    @Operation(
            summary = "장바구니 삭제",
            description = "장바구니를 삭제하는 API"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니 삭제 성공",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            examples = @ExampleObject(
                                                    value = """
                    {
                        "success":true,
                        "code":"CART_DELETE_SUCCESS",
                        "message":"장바구니가 정상적으로 삭제되었습니다.",
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
    ResponseEntity<ApiResponses<Void>> deleteCartProduct(
            CurrentUser currentUser,
            Long cartId
    );
}
