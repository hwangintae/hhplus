package org.hhplus.ecommerce.cart.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cart.entity.CartItem;

@Getter
public class CartItemDomain {

    private final Long id;
    private final Long cartId;
    private final Long itemId;
    private final int cnt;
    private boolean deleteAt;

    @Builder
    protected CartItemDomain(Long id, Long cartId, Long itemId, int cnt, boolean deleteAt) {
        this.id = id;
        this.cartId = cartId;
        this.itemId = itemId;
        this.cnt = cnt;
        this.deleteAt = deleteAt;
    }

    public static CartItemDomain generateCartItemDomain(Long cartId, Long itemId, int cnt) {
        return CartItemDomain.builder()
                .cartId(cartId)
                .itemId(itemId)
                .cnt(cnt)
                .deleteAt(false)
                .build();
    }

    public CartItem toEntity() {
        return CartItem.builder()
                .id(this.id)
                .cartId(this.cartId)
                .itemId(this.itemId)
                .cnt(this.cnt)
                .deleteAt(this.deleteAt)
                .build();
    }

    public void deleteCartItem() {
        this.deleteAt = true;
    }
}
