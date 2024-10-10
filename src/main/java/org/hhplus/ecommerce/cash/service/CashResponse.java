package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CashResponse {
    private final Long userId;
    private final BigDecimal amount;

    @Builder
    protected CashResponse(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
