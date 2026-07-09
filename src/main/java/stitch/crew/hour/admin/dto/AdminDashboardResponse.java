package stitch.crew.hour.admin.dto;

import java.util.List;

public record AdminDashboardResponse(
        Long todaySales,
        Long totalSales,
        Long todayOrderCount,
        Long paidOrderCount,
        Long inDeliveryOrderCount,
        Long totalUserCount,
        Long todayUserCount,
        Long activeProductCount,
        Long soldOutProductCount,
        List<RecentOrderResponse> recentOrders,
        List<TopProductResponse> topProducts
) {
}
