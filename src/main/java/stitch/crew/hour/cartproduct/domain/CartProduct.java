package stitch.crew.hour.cartproduct.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.cart.domain.Cart;
import stitch.crew.hour.product.domain.Product;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public CartProduct(
            Cart cart,
            Product product,
            Long amount
    ){
        if(amount <= 1L) this.amount = 1L;
        else this.amount = amount;
        this.product = product;
        this.cart = cart;
    }
}
