package stitch.crew.hour.admin.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import stitch.crew.hour.admin.dto.RecentOrderResponse;
import stitch.crew.hour.admin.dto.TopProductResponse;
import stitch.crew.hour.order.constant.OrderStatus;
import stitch.crew.hour.order.domain.QOrder;
import stitch.crew.hour.orderproduct.domain.QOrderProduct;
import stitch.crew.hour.product.constant.ProductStatus;
import stitch.crew.hour.product.domain.QProduct;
import stitch.crew.hour.user.domain.QUser;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminDashboardRepositoryImpl implements AdminDashboardRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QOrder qOrder = QOrder.order;
    private final QOrderProduct qOrderProduct = QOrderProduct.orderProduct;
    private final QProduct qProduct = QProduct.product;
    private final QUser qUser = QUser.user;

    @Override
    public Long sumSales(List<OrderStatus> salesStatuses) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(orderTotalPriceSum())
                        .from(qOrder)
                        .where(qOrder.orderStatus.in(salesStatuses))
                        .fetchOne()
        );
    }

    @Override
    public Long sumSalesBetween(List<OrderStatus> salesStatuses, LocalDateTime start, LocalDateTime end) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(orderTotalPriceSum())
                        .from(qOrder)
                        .where(
                                qOrder.orderStatus.in(salesStatuses),
                                qOrder.createdAt.goe(start),
                                qOrder.createdAt.lt(end)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Long countOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(qOrder.count())
                        .from(qOrder)
                        .where(
                                qOrder.createdAt.goe(start),
                                qOrder.createdAt.lt(end)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Long countOrdersByStatus(OrderStatus orderStatus) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(qOrder.count())
                        .from(qOrder)
                        .where(qOrder.orderStatus.eq(orderStatus))
                        .fetchOne()
        );
    }

    @Override
    public Long countUsersNotDeleted() {
        return zeroIfNull(
                jpaQueryFactory
                        .select(qUser.count())
                        .from(qUser)
                        .where(qUser.deletedAt.isNull())
                        .fetchOne()
        );
    }

    @Override
    public Long countUsersCreatedBetweenAndNotDeleted(LocalDateTime start, LocalDateTime end) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(qUser.count())
                        .from(qUser)
                        .where(
                                qUser.createdAt.goe(start),
                                qUser.createdAt.lt(end),
                                qUser.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Long countProductsByStatus(ProductStatus productStatus) {
        return zeroIfNull(
                jpaQueryFactory
                        .select(qProduct.count())
                        .from(qProduct)
                        .where(qProduct.status.eq(productStatus))
                        .fetchOne()
        );
    }

    @Override
    public List<RecentOrderResponse> findRecentOrders(int limit) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        RecentOrderResponse.class,
                        qOrder.orderNumber,
                        qOrder.ordererName,
                        qOrder.totalPrice,
                        qOrder.orderStatus.stringValue(),
                        qOrder.createdAt
                ))
                .from(qOrder)
                .orderBy(qOrder.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<TopProductResponse> findTopProducts(
        List<OrderStatus> salesStatuses,
        int limit
    ) {
        NumberExpression<Long> totalQuantity = orderProductAmountSum();
        NumberExpression<Long> totalSales = orderProductSalesSum();

        List<Tuple> rows = jpaQueryFactory
                .select(
                        qOrderProduct.productId,
                        qOrderProduct.name,
                        totalQuantity,
                        totalSales,
                        qProduct.thumbnail
                )
                .from(qOrderProduct)
                .join(qOrderProduct.order, qOrder)
                .leftJoin(qProduct).on(qProduct.id.eq(qOrderProduct.productId))
                .where(
                        qOrder.orderStatus.in(salesStatuses),
                        qOrderProduct.productId.isNotNull()
                )
                .groupBy(qOrderProduct.productId, qOrderProduct.name, qProduct.thumbnail)
                .orderBy(
                        totalSales.desc(),
                        totalQuantity.desc()
                )
                .limit(limit)
                .fetch();

        return rows.stream()
                .map(row -> new TopProductResponse(
                        row.get(qOrderProduct.productId),
                        row.get(qOrderProduct.name),
                        zeroIfNull(row.get(totalQuantity)),
                        zeroIfNull(row.get(totalSales)),
                        row.get(qProduct.thumbnail)
                ))
                .toList();
    }

    private NumberExpression<Long> orderTotalPriceSum() {
        return Expressions.numberTemplate(Long.class, "sum({0})", qOrder.totalPrice);
    }

    private NumberExpression<Long> orderProductAmountSum() {
        return Expressions.numberTemplate(Long.class, "sum({0})", qOrderProduct.amount);
    }

    private NumberExpression<Long> orderProductSalesSum() {
        return Expressions.numberTemplate(Long.class, "sum({0} * {1})", qOrderProduct.price, qOrderProduct.amount);
    }

    private Long zeroIfNull(Long value) {
        return value == null ? 0L : value;
    }
}
