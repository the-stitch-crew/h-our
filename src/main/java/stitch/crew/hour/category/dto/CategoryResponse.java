package stitch.crew.hour.category.dto;

import java.util.List;

public record CategoryResponse(
        List<CategoryInfo> categoryList
) {
}
