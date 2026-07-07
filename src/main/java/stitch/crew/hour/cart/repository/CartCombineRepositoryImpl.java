package stitch.crew.hour.cart.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.cartproduct.repository.CartProductRepository;

@Repository
@RequiredArgsConstructor
public class CartCombineRepositoryImpl implements CartCombineRepository {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Cart findCartByIdOrThrow(Long cartId) {
        return cartRepository.findByIdOrThrow(cartId);
    }

    @Override
    public CartProduct saveCartProduct(CartProduct cartProduct) {
        return cartProductRepository.save(cartProduct);
    }

    @Override
    public CartProduct findCartProductByIdOrThrow(Long cartProductId) {
        return cartProductRepository.findByIdOrThrow(cartProductId);
    }

    @Override
    public void deleteCartProduct(CartProduct cartProduct) {
        cartProductRepository.delete(cartProduct);
    }

    @Override
    public void deleteCart(Cart cart) {
        cartRepository.delete(cart);
        cartRepository.flush();
    }
}
