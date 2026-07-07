package stitch.crew.hour.category.dto;

import stitch.crew.hour.category.domain.Category;

public record CategoryResponse(
        Long id,
        String name,
        String thumbnail
) {
    public static CategoryResponse from(Category category, String thumbnail) {
        return new CategoryResponse(category.getId(), category.getName(), thumbnail);
    }
}
