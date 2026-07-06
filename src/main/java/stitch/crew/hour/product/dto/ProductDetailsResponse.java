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
        return new ProductDetailsResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                product.getStatus().name(),
                product.getSummary(),
                product.getDescription(),
                product.getViewCount(),
                product.getSalesCount(),
                product.getCategory().getName()
        );
    }
}
