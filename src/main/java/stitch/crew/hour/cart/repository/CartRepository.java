package stitch.crew.hour.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.common.exception.BusinessException;
import stitch.crew.hour.common.exception.ErrorCode;

public interface CartRepository extends JpaRepository<Cart, Long> {

    default Cart findByIdOrThrow(Long cartId){
        return findById(cartId).orElseThrow(
                ()-> new BusinessException(ErrorCode.CART_NOT_FOUND)
        );
    }
}
