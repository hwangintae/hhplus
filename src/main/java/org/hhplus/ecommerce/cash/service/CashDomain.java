package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cash.entity.Cash;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;

import java.math.BigDecimal;

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
        this.amount += amount;
    }

    public void sub(long amount) {
        if (this.amount - amount < 0) {
            throw new EcommerceBadRequestException(INSUFFICIENT_USER_CASH);
        }

        this.amount -= amount;
    }
}
