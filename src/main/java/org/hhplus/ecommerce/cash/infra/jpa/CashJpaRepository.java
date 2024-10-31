package org.hhplus.ecommerce.cash.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CashJpaRepository extends JpaRepository<Cash, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Cash> findByUserId(Long userId);
}
