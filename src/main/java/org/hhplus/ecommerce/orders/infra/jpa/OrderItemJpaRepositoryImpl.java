package org.hhplus.ecommerce.orders.infra.jpa;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.service.PopularItemsResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hhplus.ecommerce.orders.infra.jpa.QOrderItem.orderItem;

@RequiredArgsConstructor
public class OrderItemJpaRepositoryImpl implements OrderItemJpaRepositoryDsl {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PopularItemsResult> findPopularItems(int from, int limit) {

        LocalDateTime now = LocalDate.now().atTime(LocalTime.MIDNIGHT);
        LocalDateTime toDateTime = now.minusDays(1);
        LocalDateTime fromDateTime = toDateTime.minusDays(from);


        return jpaQueryFactory.select(Projections.constructor(PopularItemsResult.class,
                        orderItem.itemId,
                        orderItem.itemCnt.sum()))
                .from(orderItem)
                .where(orderItem.createdAt.between(fromDateTime, toDateTime))
                .groupBy(orderItem.itemId)
                .orderBy(orderItem.itemCnt.sum().desc())
                .limit(limit)
                .fetch();
    }
}
