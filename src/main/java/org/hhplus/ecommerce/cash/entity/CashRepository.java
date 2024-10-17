package org.hhplus.ecommerce.cash.entity;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CashRepository extends JpaRepository<Cash, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Cash> findByUserId(Long userId);
}
