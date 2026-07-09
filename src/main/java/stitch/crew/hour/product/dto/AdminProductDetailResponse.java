package stitch.crew.hour.product.dto;

import stitch.crew.hour.product.domain.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminProductDetailResponse(
        Long productId,
        String name,
        Long price,
        String thumbnail,
        String status,
        String summary,
        String description,
        String categoryName,
        Boolean isMain,
        Long viewCount,
        Integer salesCount,
        LocalDate lastErolledToMain,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static AdminProductDetailResponse from(Product product) {
        return new AdminProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getThumbnail(),
                product.getStatus().name(),
                product.getSummary(),
                product.getDescription(),
                product.getCategory().getName(),
                product.getIsMain(),
                product.getViewCount(),
                product.getSalesCount(),
                product.getLastErolledToMain(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt()
        );
    }
}
