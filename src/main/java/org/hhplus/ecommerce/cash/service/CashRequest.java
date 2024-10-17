package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CashRequest {

    private Long userId;
    private long amount;

    @Builder
    protected CashRequest(Long userId, long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
