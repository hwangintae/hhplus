package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequestsWithUserId {
    private Long userId;
    private List<OrderRequest> orderRequests;

    @Builder
    protected OrderRequestsWithUserId(Long userId, List<OrderRequest> orderRequests) {
        this.userId = userId;
        this.orderRequests = orderRequests;
    }
}
