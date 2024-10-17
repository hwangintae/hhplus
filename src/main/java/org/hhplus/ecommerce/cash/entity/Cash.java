package org.hhplus.ecommerce.cash.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashDomain;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
public class Cash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    @Builder
    protected Cash(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public CashDomain toDomain() {
        return CashDomain.builder()
                .id(this.id)
                .userId(this.userId)
                .amount(this.amount)
                .build();
    }
}
