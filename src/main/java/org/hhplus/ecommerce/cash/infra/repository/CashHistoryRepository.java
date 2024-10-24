package org.hhplus.ecommerce.cash.infra.repository;

import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;

public interface CashHistoryRepository {
    CashHistory save(CashHistory cashHistory);
}
