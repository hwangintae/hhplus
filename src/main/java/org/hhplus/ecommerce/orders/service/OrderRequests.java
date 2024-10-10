package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequests {

    private List<OrderRequest> orderRequests;

    @Builder
    protected OrderRequests(List<OrderRequest> orderRequests) {
        this.orderRequests = orderRequests;
    }
}
