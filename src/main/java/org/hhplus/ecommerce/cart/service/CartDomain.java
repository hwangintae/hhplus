package org.hhplus.ecommerce.cart.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cart.entity.Cart;

@Getter
public class CartDomain {
    private final Long id;
    private final Long userId;

    @Builder
    protected CartDomain(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static CartDomain generateCartDomain(Long userId) {
        return CartDomain.builder()
                .id(userId)
                .build();
    }

    public Cart toEntity() {
        return Cart.builder()
                .id(this.id)
                .userId(this.userId)
                .build();
    }


}
