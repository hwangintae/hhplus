package org.hhplus.ecommerce.cash.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;

import java.util.List;

@Getter
@NoArgsConstructor
public class CashRequest {

    private Long userId;
    private List<OrderItemInfo> orderItemInfos;
    private long amount;

    @Builder
    protected CashRequest(Long userId, List<OrderItemInfo> orderItemInfos, long amount) {
        this.userId = userId;
        this.orderItemInfos = orderItemInfos;
        this.amount = amount;
    }
}
