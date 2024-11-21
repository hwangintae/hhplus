package org.hhplus.ecommerce.orders.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersOutboxJpaRepository extends JpaRepository<OrdersOutbox, Long> {
    Optional<OrdersOutbox> findByOrderId(Long orderId);
    List<OrdersOutbox> findByStatus(OrdersOutboxStatus status);
}
