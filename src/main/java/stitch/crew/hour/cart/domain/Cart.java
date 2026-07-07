package stitch.crew.hour.cart.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.cartproduct.domain.CartProduct;
import stitch.crew.hour.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED                                  )
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartProduct> cartProducts = new ArrayList<>();

    public Cart(
            User user
    ){
        this.user = user;
        user.addCart(this);
    }

    public void addCart(CartProduct cartProduct){
        this.cartProducts.add(cartProduct);
    }
    public void removeCart(CartProduct cartProduct){this.cartProducts.remove(cartProduct); }
}
