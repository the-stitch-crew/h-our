package stitch.crew.hour.order.dto;

import stitch.crew.hour.orderproduct.domain.OrderProduct;

public record AdminOrderProductResponse(
        Long productId,
        String name,
        Long amount,
        Long price,
        String option
) {
    public static AdminOrderProductResponse from(OrderProduct orderProduct) {
        return new AdminOrderProductResponse(
                orderProduct.getProductId(),
                orderProduct.getName(),
                orderProduct.getAmount(),
                orderProduct.getPrice(),
                orderProduct.getOption()
        );
    }
}
