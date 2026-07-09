package stitch.crew.hour.product.dto;

import stitch.crew.hour.product.constant.ProductStatus;

public record ProductStatusUpdateRequest(
        ProductStatus status
) {
}
