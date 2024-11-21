package org.hhplus.ecommerce.item.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockOutboxJpaRepository extends JpaRepository<StockOutbox, Long> {
   Optional<StockOutbox> findByOrderId(Long orderId);
   List<StockOutbox> findByStatus(StockOutboxStatus status);
}
