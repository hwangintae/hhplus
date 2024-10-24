package org.hhplus.ecommerce.cash.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistoryJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CashHistoryRepositoryImpl implements CashHistoryRepository {

    private final CashHistoryJpaRepository cashHistoryJpaRepository;

    @Override
    public CashHistory save(CashHistory cashHistory) {
        return cashHistoryJpaRepository.save(cashHistory);
    }
}
