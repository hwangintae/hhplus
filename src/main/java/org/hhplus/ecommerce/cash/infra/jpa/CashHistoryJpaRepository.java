package org.hhplus.ecommerce.cash.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CashHistoryJpaRepository extends JpaRepository<CashHistory, Long> {
}
