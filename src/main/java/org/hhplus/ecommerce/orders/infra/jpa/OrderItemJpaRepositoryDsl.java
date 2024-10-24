package org.hhplus.ecommerce.orders.infra.jpa;

import org.hhplus.ecommerce.orders.service.PopularItemsResult;

import java.util.List;

public interface OrderItemJpaRepositoryDsl {
    List<PopularItemsResult> findPopularItems(int from, int limit);
}
