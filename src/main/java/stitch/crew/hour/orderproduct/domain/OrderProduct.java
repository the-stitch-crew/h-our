package stitch.crew.hour.orderproduct.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stitch.crew.hour.order.domain.Order;


@Entity
@Getter
@Table(name = "order_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long price;

    private Long productId;

    @Column(
            nullable = false,
            name = "product_option",
            length = 255
    )
    private String option;

    @ManyToOne
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    public OrderProduct(
            String name,
            Long amount,
            Long price,
            Long productId,
            String option,
            Order order
    ){
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.option = option;
        this.order = order;
        if( productId != null ) this.productId = productId;
    }

    public void setOrder(Order order){
        this.order = order;
    }


}
