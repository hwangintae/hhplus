package org.hhplus.ecommerce.cash.controller;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cash.service.CashDomain;

import java.math.BigDecimal;

@Getter
public class CashResponse {
    private final Long userId;
    private final long amount;

    @Builder
    protected CashResponse(Long userId, long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public static CashResponse of(CashDomain domain) {
        return CashResponse.builder()
                .userId(domain.getUserId())
                .amount(domain.getAmount())
                .build();
    }
}
