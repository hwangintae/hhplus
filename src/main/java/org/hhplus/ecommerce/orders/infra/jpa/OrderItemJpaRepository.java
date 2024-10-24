package org.hhplus.ecommerce.orders.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long>, OrderItemJpaRepositoryDsl {
    List<OrderItem> findByOrdersIdIn(List<Long> ordersId);
}
