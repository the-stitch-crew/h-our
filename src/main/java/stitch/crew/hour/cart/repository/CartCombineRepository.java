package stitch.crew.hour.cart.repository;

import org.springframework.stereotype.Repository;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cartproduct.domain.CartProduct;

@Repository
public interface CartCombineRepository {

    Cart saveCart(Cart cart);
    Cart findCartByIdOrThrow(Long cartId);
    CartProduct saveCartProduct(CartProduct cartProduct);
    CartProduct findCartProductByIdOrThrow(Long cartProductId);
    void deleteCartProduct(CartProduct cartProduct);
    void deleteCart(Cart cart);
}
