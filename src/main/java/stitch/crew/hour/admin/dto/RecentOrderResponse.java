package stitch.crew.hour.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecentOrderResponse(
        UUID orderNumber,
        String ordererName,
        Integer totalPrice,
        String orderStatus,
        LocalDateTime createdAt
) {
}
