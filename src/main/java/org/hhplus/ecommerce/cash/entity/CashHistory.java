package org.hhplus.ecommerce.cash.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
