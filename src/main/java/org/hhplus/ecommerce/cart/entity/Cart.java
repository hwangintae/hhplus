package org.hhplus.ecommerce.cart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.cart.service.CartDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Builder
    protected Cart(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public CartDomain toDomain() {
        return CartDomain.builder()
                .id(this.id)
                .userId(this.userId)
                .build();
    }
}
