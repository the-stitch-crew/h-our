package stitch.crew.hour.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stitch.crew.hour.admin.dto.AdminDashboardResponse;
import stitch.crew.hour.admin.repository.AdminDashboardRepository;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.product.constant.ProductStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final int RECENT_ORDER_LIMIT = 5;
    private static final int TOP_PRODUCT_LIMIT = 5;
    private static final List<OrderStatus> SALES_STATUSES = List.of(
            OrderStatus.PURCHASED,
            OrderStatus.IN_DELIVERY,
            OrderStatus.DELIVERED,
            OrderStatus.COMPLETE
    );

    private final AdminDashboardRepository adminDashboardRepository;

    public AdminDashboardResponse getDashboardInfo() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return new AdminDashboardResponse(
                adminDashboardRepository.sumSalesBetween(SALES_STATUSES, start, end),
                adminDashboardRepository.sumSales(SALES_STATUSES),
                adminDashboardRepository.countOrdersBetween(start, end),
                adminDashboardRepository.countOrdersByStatus(OrderStatus.PURCHASED),
                adminDashboardRepository.countOrdersByStatus(OrderStatus.IN_DELIVERY),
                adminDashboardRepository.countUsersNotDeleted(),
                adminDashboardRepository.countUsersCreatedBetweenAndNotDeleted(start, end),
                adminDashboardRepository.countProductsByStatus(ProductStatus.ACTIVATED),
                adminDashboardRepository.countProductsByStatus(ProductStatus.SOLD_OUT),
                adminDashboardRepository.findRecentOrders(RECENT_ORDER_LIMIT),
                adminDashboardRepository.findTopProducts(SALES_STATUSES, TOP_PRODUCT_LIMIT)
        );
    }
}
