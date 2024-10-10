package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CashDomain {
    private final Long id;
    private final Long userId;
    private final BigDecimal amount;

    @Builder
    protected CashDomain(Long id, Long userId, BigDecimal amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }
}
