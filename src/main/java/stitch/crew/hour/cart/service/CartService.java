package stitch.crew.hour.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cart.dto.CartDetailResponse;
import stitch.crew.hour.cart.dto.AddCartProductRequest;
import stitch.crew.hour.cart.dto.UpdateCartProductRequest;
import stitch.crew.hour.cart.repository.CartCombineRepository;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.common.exception.ErrorCode;
import stitch.crew.hour.common.util.PreConditions;
import stitch.crew.hour.product.domain.Product;
import stitch.crew.hour.product.repository.ProductRepository;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class CartService {

    private final UserRepository userRepository;
    private final CartCombineRepository cartCombineRepository;
    private final ProductRepository productRepository;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public CartDetailResponse createCart(
            Long userId
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        PreConditions.validate(
                foundedUser.getCart() == null,
                ErrorCode.CART_ALREADY_EXISTS
        );

        Cart saved = cartCombineRepository.saveCart(
                new Cart(foundedUser)
        );

        return CartDetailResponse.from(
                saved,
                userId
        );
    }

    @PreAuthorize("isAuthenticated() && #userId == authentication.principal.id")
    public CartDetailResponse getCartByMe(
            Long userId
    ){
        User foundedUser = userRepository.findByIdOrthrow(userId);

        Cart cart = foundedUser.getCart();

        PreConditions.validate(
                cart != null,
                ErrorCode.NO_CART
        );

        return CartDetailResponse.from(
                cart,
                userId
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public CartDetailResponse addCartProductToCart(
            Long userId,
            Long cartId,
            AddCartProductRequest request
    ){
        Cart cart = cartCombineRepository.findCartByIdOrThrow(cartId);

        Product foundedProduct = productRepository.findByIdOrThrow(request.productId());

        cartCombineRepository.saveCartProduct(
                new CartProduct(
                        cart,
                        foundedProduct,
                        request.amount()
                )
        );

        return CartDetailResponse.from(
                cart,
                userId
        );
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public CartDetailResponse updateCartProduct(
            Long userId,
            Long cartId,
            UpdateCartProductRequest request
    ){
        Cart foundedCart = cartCombineRepository.findCartByIdOrThrow(cartId);
        CartProduct foundedCardProduct = cartCombineRepository.findCartProductByIdOrThrow(
                request.cartProductId()
        );

        PreConditions.validate(
                foundedCardProduct.getCart().equals(foundedCart),
                ErrorCode.NOT_VALID_CART_PRODUCT
        );

        if( request.amount() == 0 ){
            foundedCart.removeCart(foundedCardProduct);
            cartCombineRepository.deleteCartProduct(foundedCardProduct);
            return CartDetailResponse.from(
                    foundedCart,
                    userId
            );
        } else {
            foundedCardProduct.updateCartProduct(
                    request.option(),
                    request.amount()
            );
            return CartDetailResponse.from(
                    foundedCart,
                    userId
            );
        }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteCart(
            Long userId,
            Long cartId
    ){
        Cart foundedCart = cartCombineRepository.findCartByIdOrThrow(cartId);

        PreConditions.validate(
                foundedCart.getUser().getId().equals(userId),
                ErrorCode.NO_MATCH_CART_OWNER
        );

        User foundedUser = userRepository.findByIdOrthrow(userId);
        foundedUser.setNoCart();

        cartCombineRepository.deleteCart(foundedCart);
    }
}
