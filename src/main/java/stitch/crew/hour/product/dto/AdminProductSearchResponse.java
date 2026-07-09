package stitch.crew.hour.product.dto;

import stitch.crew.hour.product.domain.Product;

import java.time.LocalDateTime;

public record AdminProductSearchResponse(
        Long productId,
        String name,
        Long price,
        String thumbnail,
        String status,
        String categoryName,
        Boolean isMain,
        Long viewCount,
        Integer salesCount,
        LocalDateTime createdAt
) {
    public static AdminProductSearchResponse from(Product product) {
        return new AdminProductSearchResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                product.getStatus().name(),
                product.getCategory().getName(),
                product.getIsMain(),
                product.getViewCount(),
                product.getSalesCount(),
                product.getCreatedAt()
        );
    }
}
