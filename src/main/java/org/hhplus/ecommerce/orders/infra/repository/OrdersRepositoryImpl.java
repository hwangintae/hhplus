package org.hhplus.ecommerce.orders.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.infra.jpa.Orders;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdersRepositoryImpl implements OrdersRepository {

    private final OrdersJpaRepository ordersJpaRepository;

    @Override
    public List<Orders> findByUserId(Long userId) {
        return ordersJpaRepository.findByUserId(userId);
    }

    @Override
    public Orders save(Orders orders) {
        return ordersJpaRepository.save(orders);
    }
}
