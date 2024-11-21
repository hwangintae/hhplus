package org.hhplus.ecommerce.orders.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersDataPlatformOutboxJpaRepository extends JpaRepository<OrdersDataPlatformOutbox, Long> {
    Optional<OrdersDataPlatformOutbox> findByOrderId(Long orderId);
    List<OrdersDataPlatformOutbox> findByStatus(OrdersOutboxStatus status);
}
