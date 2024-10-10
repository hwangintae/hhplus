package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {

    private Long userId;
    private Long itemId;
    private int cnt;

    @Builder
    protected OrderRequest(Long userId, Long itemId, int cnt) {
        this.userId = userId;
        this.itemId = itemId;
        this.cnt = cnt;
    }
}
