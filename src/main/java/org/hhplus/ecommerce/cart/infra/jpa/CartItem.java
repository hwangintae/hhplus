package org.hhplus.ecommerce.cart.infra.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.cart.service.CartItemDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cartId;

    private Long itemId;

    private int cnt;

    private boolean deleteAt;

    @Builder
    protected CartItem(Long id, Long cartId, Long itemId, int cnt, boolean deleteAt) {
        this.id = id;
        this.cartId = cartId;
        this.itemId = itemId;
        this.cnt = cnt;
        this.deleteAt = deleteAt;
    }

    public CartItemDomain toDomain() {
        return CartItemDomain.builder()
                .id(this.id)
                .cartId(this.cartId)
                .itemId(this.itemId)
                .cnt(this.cnt)
                .deleteAt(this.deleteAt)
                .build();
    }
}
