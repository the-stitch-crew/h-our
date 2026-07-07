package stitch.crew.hour.cartproduct.dto;

import stitch.crew.hour.cartproduct.domain.CartProduct;

public record CartProductDetailResponse(
        Long cartProductId,
        Integer amount,
        String productName,
        Long price,
        Long totalPrice
) {
    public static CartProductDetailResponse from(CartProduct cartProduct){
        return new CartProductDetailResponse(
                cartProduct.getId(),
                cartProduct.getAmount().intValue(),
                cartProduct.getProductName(),
                cartProduct.getProductPrice(),
                cartProduct.getTotalPrice()
        );
    }
}
