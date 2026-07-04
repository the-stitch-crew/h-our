package stitch.crew.hour.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import stitch.crew.hour.common.domain.BaseEntity;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.orderproduct.domain.OrderProduct;
import stitch.crew.hour.user.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private Long deliveryFee;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false, length = 50)
    private String receiverName;

    @Column(nullable = false, length = 50)
    private String phoneNumber;

    @Column(length = 255)
    private String request;

    @Column(nullable = false, length = 50)
    private String ordererName;

    @Column(nullable = false, length = 50)
    private String receiverPhoneNumber;

    @Column(nullable = false, length = 50)
    private UUID orderNumber;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProduct = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "orderer_id")
    private User orderer;

    public Order(
        User user,
        List<OrderProduct> orderProducts,
        Long deliveryFee,
        String address,
        String postalCode,
        String receiverName,
        String phoneNumber,
        String request,
        String ordererName,
        String receiverPhoneNumber
    ){
        this.orderer = user;
        this.orderProduct = orderProducts;
        this.totalPrice = calTotalPrice(orderProducts);
        this.orderStatus = OrderStatus.ORDERED;
        this.deliveryFee = deliveryFee;
        this.address = address;
        this.postalCode = postalCode;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
        if (Strings.isNotBlank(request)) this.request = request;
        if (Strings.isNotBlank(ordererName)) this.ordererName = user.getUserName();

        user.addOrder(this);
    }

    public Integer calTotalPrice(List<OrderProduct> lst){
        Long price = 0L;
        for(OrderProduct op : lst) price += op.getPrice();
        return price.intValue();
    }

    public void switchStatus(OrderStatus status){
        this.orderStatus = status;
    }
}
