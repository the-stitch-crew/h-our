package stitch.crew.hour.cart.dto;

import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.cartproduct.dto.CartProductDetailResponse;
import stitch.crew.hour.user.domain.User;

import java.util.ArrayList;
import java.util.List;

public record CartDetailResponse(
    Long cartId,
    Long userId,
    List<CartProductDetailResponse> products,
    Long totalPrice
) {
    public static CartDetailResponse from(Cart cart, Long userId){
        Long totalPrice = 0L;
        List<CartProductDetailResponse> lst = new ArrayList<>();

        if(!cart.getCartProducts().isEmpty()){
            for(CartProduct cartProduct : cart.getCartProducts()){
                totalPrice += cartProduct.getTotalPrice();
                lst.add(CartProductDetailResponse.from(cartProduct));
            }
        }

        return new CartDetailResponse(
                cart.getId(),
                userId,
                lst,
                totalPrice
        );
    }
}
