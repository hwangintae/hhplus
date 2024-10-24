package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;

@Getter
public class CashHistoryDomain {

    private final Long id;
    private final Long cashId;
    private final Long amount;
    private final TransactionType type;

    @Builder
    protected CashHistoryDomain(Long id, Long cashId, Long amount, TransactionType type) {
        this.id = id;
        this.cashId = cashId;
        this.amount = amount;
        this.type = type;
    }

    public CashHistory toEntity() {
        return CashHistory.builder()
                .id(this.id)
                .cashId(this.cashId)
                .amount(this.amount)
                .type(this.type)
                .build();
    }
}
