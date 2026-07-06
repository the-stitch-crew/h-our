package stitch.crew.hour.product.dto;

import stitch.crew.hour.product.domain.Product;

public record ProductCreateResponse(
        String productName,
        Long price,
        Long productId
) {
    public static ProductCreateResponse from(
            Product product
    ){
        return new ProductCreateResponse(
                product.getName(),
                product.getPrice(),
                product.getId()
        );
    }
}
