package org.hhplus.ecommerce.cash.infra.repository;

import org.hhplus.ecommerce.cash.infra.jpa.Cash;

public interface CashRepository {
    Cash findByUserId(Long userId);
    Cash save(Cash cash);
}
