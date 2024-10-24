package org.hhplus.ecommerce.orders.infra.repository;

import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;
import org.hhplus.ecommerce.orders.service.PopularItemsResult;

import java.util.List;

public interface OrderItemRepository {
    List<OrderItem> findByOrdersIdIn(List<Long> ordersId);

    List<OrderItem> save(List<OrderItem> orderItems);

    List<PopularItemsResult> findPopularItems(int from, int limit);
}
