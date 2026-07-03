package stitch.crew.hour.category.dto;

import stitch.crew.hour.category.domain.Category;

public record CategoryInfo(
        Long id,
        String name,
        String thumbnail
) {
    public static CategoryInfo from(Category category) {
        return new CategoryInfo(category.getId(), category.getName(), category.getThumbnail());
    }
}
