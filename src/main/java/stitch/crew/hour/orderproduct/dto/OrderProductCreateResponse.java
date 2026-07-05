package stitch.crew.hour.orderproduct.dto;

import stitch.crew.hour.orderproduct.domain.OrderProduct;

public record OrderProductCreateResponse(
    String name,
    Long amount,
    Long price,
    Long productId
) {
    public static OrderProductCreateResponse from(OrderProduct orderProduct){
        return new OrderProductCreateResponse(
                orderProduct.getName(),
                orderProduct.getAmount(),
                orderProduct.getPrice(),
                orderProduct.getProductId()
        );
    }
}
