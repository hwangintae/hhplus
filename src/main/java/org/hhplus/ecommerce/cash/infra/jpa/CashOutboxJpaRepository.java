package org.hhplus.ecommerce.cash.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashOutboxJpaRepository extends JpaRepository<CashOutbox, Long> {
    Optional<CashOutbox> findByOrderId(Long orderId);
    List<CashOutbox> findByStatus(CashOutboxStatus status);
}
