package stitch.crew.hour.product.dto;

import stitch.crew.hour.product.domain.Product;

public record ProductDetailsResponse(
        Long productId,
        String name,
        Long price,
        String thumbnail,
        String productStatus,
        String summary,
        String description,
        Long viewCount,
        Integer salesCount,
        String categoryName
) {
    public static ProductDetailsResponse from(
            Product product
    ){
        return from(product, product.getThumbnail());
    }

    public static ProductDetailsResponse from(
            Product product,
            String thumbnail
    ){
        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                thumbnail,
                product.getStatus().name(),
                product.getSummary(),
                product.getDescription(),
                product.getViewCount(),
                product.getSalesCount(),
                product.getCategory().getName()
        );
    }
}
