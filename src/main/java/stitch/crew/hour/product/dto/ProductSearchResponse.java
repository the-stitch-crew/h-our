package stitch.crew.hour.product.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ProductSearchResponse(
        Long productId,
        String name,
        Long price,
        String thumbnail,
        String productStatus,
        String summary
) {
    @QueryProjection
    public ProductSearchResponse{
    }
}
