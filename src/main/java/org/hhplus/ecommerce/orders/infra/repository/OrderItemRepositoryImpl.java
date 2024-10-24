package org.hhplus.ecommerce.orders.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItemJpaRepository;
import org.hhplus.ecommerce.orders.service.PopularItemsResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public List<OrderItem> findByOrdersIdIn(List<Long> ordersId) {
        return orderItemJpaRepository.findByOrdersIdIn(ordersId);
    }

    @Override
    public List<OrderItem> save(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }

    @Override
    public List<PopularItemsResult> findPopularItems(int from, int limit) {
        return orderItemJpaRepository.findPopularItems(from, limit);
    }
}
