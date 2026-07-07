package stitch.crew.hour.cartproduct.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
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

    @Column(nullable = false, length = 50)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(
            nullable = false,
            length = 255
    )
    private String option;

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
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.option = "";
        this.cart = cart;
        this.cart.addCart(this);
        this.totalPrice = calPrice();
    }

    public void updateCartProduct(
            String option,
            Long amount
    ){
        if (Strings.isNotBlank(option)) this.option = option;
        this.amount = amount;
        this.totalPrice = calPrice();
    }

    public void setOption(String option){
        this.option = option;
    }

    public Long calPrice(){
        return this.productPrice * this.amount;
    }
}
