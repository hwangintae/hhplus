package org.hhplus.ecommerce.orders.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.infra.jpa.Orders;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrdersRepositoryImpl implements OrdersRepository {

    private final OrdersJpaRepository ordersJpaRepository;

    @Override
    public List<Orders> findByUserId(Long userId) {
        return ordersJpaRepository.findByUserId(userId);
    }

    @Override
    public Orders findById(Long id) {
        return ordersJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문 정보가 없습니다."));
    }

    @Override
    public Orders save(Orders orders) {
        return ordersJpaRepository.save(orders);
    }
}
