package stitch.crew.hour.orderproduct.dto;

import stitch.crew.hour.orderproduct.domain.OrderProduct;

public record OrderProductDetailResponse(
    String name,
    Long amount,
    Long price,
    Long productId
) {
    public static OrderProductDetailResponse from(OrderProduct orderProduct){
        return new OrderProductDetailResponse(
                orderProduct.getName(),
                orderProduct.getAmount(),
                orderProduct.getPrice(),
                orderProduct.getProductId()
        );
    }
}
