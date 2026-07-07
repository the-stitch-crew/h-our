package stitch.crew.hour.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import stitch.crew.hour.cart.dto.AddCartProductRequest;
import stitch.crew.hour.cart.dto.CartDetailResponse;
import stitch.crew.hour.cart.dto.UpdateCartProductRequest;
import stitch.crew.hour.cart.service.CartService;
import stitch.crew.hour.common.response.ApiResponses;
import stitch.crew.hour.common.response.ApiResult;
import stitch.crew.hour.common.response.SuccessCode;
import stitch.crew.hour.user.domain.CurrentUser;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController implements CartSwaggerSupporter {

    private final CartService cartService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponses<CartDetailResponse>> getCart(
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return ApiResult.ok(
                SuccessCode.CART_RETRIEVE_SUCCESS,
                cartService.getCartByMe(currentUser.getId())
        );
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponses<CartDetailResponse>> createCart(
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return ApiResult.created(
                SuccessCode.CART_CREATED_SUCCESS,
                cartService.createCart(currentUser.getId())
        );
    }

    @Override
    @PostMapping("/{cartId}")
    public ResponseEntity<ApiResponses<CartDetailResponse>> addProductToCart(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartId,
            @RequestBody @Valid AddCartProductRequest request
            ){
        return ApiResult.ok(
                SuccessCode.CART_ADDITION_SUCCESS,
                cartService.addCartProductToCart(
                    currentUser.getId(),
                        cartId,
                        request
                )
        );
    }

    @Override
    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponses<CartDetailResponse>> updateCartProduct(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long cartId,
            @RequestBody @Valid UpdateCartProductRequest request
    ){
        return ApiResult.ok(
            SuccessCode.CART_UPDATE_SUCCESS,
            cartService.updateCartProduct(
                    currentUser.getId(),
                    cartId,
                    request
            )
        );
    }

    @Override
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponses<Void>> deleteCartProduct(
           @AuthenticationPrincipal CurrentUser currentUser,
           @PathVariable Long cartId
    ) {
        cartService.deleteCart(
                currentUser.getId(),
                cartId
        );
        return ApiResult.ok(
            SuccessCode.CART_DELETE_SUCCESS
        );
    }
}
