package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class AddCashRequest {

    private Long userId;
    private BigDecimal amount;

    @Builder
    protected AddCashRequest(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
