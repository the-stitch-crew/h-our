package stitch.crew.hour.admin.repository;

import stitch.crew.hour.admin.dto.RecentOrderResponse;
import stitch.crew.hour.admin.dto.TopProductResponse;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.product.constant.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminDashboardRepository {

    // 판매된 개수 세기
    Long sumSales(List<OrderStatus> salesStatuses);

    // 판매된 개수 세기 기간 한정
    Long sumSalesBetween(List<OrderStatus> salesStatuses, LocalDateTime start, LocalDateTime end);

    // 기간내 주문 개수 조회
    Long countOrdersBetween(LocalDateTime start, LocalDateTime end);

    // 상태 별 주문 개수 조회
    Long countOrdersByStatus(OrderStatus orderStatus);

    // 활성된 유저 개수 조회
    Long countUsersNotDeleted();

    // 기간 내에 신규 유저 조회
    Long countUsersCreatedBetweenAndNotDeleted(LocalDateTime start, LocalDateTime end);

    // 상태별 상품 수 조회
    Long countProductsByStatus(ProductStatus productStatus);

    // 최근 주문 조회 (개수 인자로)
    List<RecentOrderResponse> findRecentOrders(int limit);

    // 판매량 별로 상품 조회 (상위 몇개)
    List<TopProductResponse> findTopProducts(List<OrderStatus> salesStatuses, int limit);
}
