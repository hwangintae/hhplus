package org.hhplus.ecommerce.cash.service;

import lombok.Getter;

@Getter
public class CashHistoryDomain {

    private Long id;
    private Long cashId;
    private TransactionType type;
}
