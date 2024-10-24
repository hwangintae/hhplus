package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ILLEGAL_AMOUNT;
import static org.hhplus.ecommerce.common.exception.EcommerceErrors.INSUFFICIENT_USER_CASH;

@Getter
public class CashDomain {
    private final Long id;
    private final Long userId;
    private long amount;

    @Builder
    protected CashDomain(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public Cash toEntity() {
        return Cash.builder()
                .id(this.id)
                .userId(this.userId)
                .amount(this.amount)
                .build();
    }

    public void add(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ILLEGAL_AMOUNT.getMessage());
        }

        this.amount += amount;
    }

    public void sub(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ILLEGAL_AMOUNT.getMessage());
        }

        if (this.amount - amount < 0) {
            throw new IllegalArgumentException(INSUFFICIENT_USER_CASH.getMessage());
        }

        this.amount -= amount;
    }
}
