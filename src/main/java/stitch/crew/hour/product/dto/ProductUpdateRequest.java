package stitch.crew.hour.product.dto;

public record ProductUpdateRequest(
        String name,
        Long price,
        String thumbnail,
        String summary,
        String description
) {
}
