package stitch.crew.hour.admin.dto;

public record TopProductResponse(
        Long productId,
        String name,
        Long totalQuantity,
        Long totalSales,
        String thumbnail
) {
}
