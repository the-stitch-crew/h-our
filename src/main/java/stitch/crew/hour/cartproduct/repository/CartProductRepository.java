package stitch.crew.hour.cartproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    default CartProduct findByIdOrThrow(Long cartId){
        return findById(cartId).orElseThrow(
                ()-> new BusinessException(ErrorCode.CARTPRODUCT_NOT_FOUNT)
        );
    }

}
