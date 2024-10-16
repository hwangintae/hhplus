package org.hhplus.ecommerce.cash.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.cash.service.TransactionType;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cashId;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Builder
    protected CashHistory(Long id, Long cashId, Long amount, TransactionType type) {
        this.id = id;
        this.cashId = cashId;
        this.amount = amount;
        this.type = type;
    }

    public static CashHistory generateChargeCashHistory (Long cashId, Long amount) {
        return CashHistory.builder()
                .cashId(cashId)
                .amount(amount)
                .type(TransactionType.CHARGE)
                .build();
    }

    public static CashHistory generateUseCashHistory (Long cashId, Long amount) {
        return CashHistory.builder()
                .cashId(cashId)
                .amount(amount)
                .type(TransactionType.USE)
                .build();
    }
}
