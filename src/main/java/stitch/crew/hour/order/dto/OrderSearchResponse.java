package stitch.crew.hour.order.dto;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.jpa.repository.Query;
import stitch.crew.hour.order.domain.Order;

import java.util.UUID;

public record OrderSearchResponse(
        UUID orderNumber,
        Integer totalPrice,
        String orderStatus
) {
    @QueryProjection
    public OrderSearchResponse{
    }

    public static OrderSearchResponse from(Order order){
        return new OrderSearchResponse(
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getOrderStatus().name()
        );
    }
}
