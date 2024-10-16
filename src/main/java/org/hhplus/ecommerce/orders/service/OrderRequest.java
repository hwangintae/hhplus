package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private Long itemId;
    private int cnt;

    @Builder
    protected OrderRequest(Long itemId, int cnt) {
        this.itemId = itemId;
        this.cnt = cnt;
    }
}
