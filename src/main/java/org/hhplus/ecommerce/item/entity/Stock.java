package org.hhplus.ecommerce.item.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.item.service.StockDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;

    private int quantity;

    @Builder
    protected Stock(Long id, Long itemId, int quantity) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static Stock of(StockDomain domain) {
        return Stock.builder()
                .id(domain.getId())
                .itemId(domain.getItemId())
                .quantity(domain.getQuantity())
                .build();
    }

    public StockDomain toDomain() {
        return StockDomain.builder()
                .id(this.id)
                .itemId(this.itemId)
                .quantity(this.quantity)
                .build();
    }
}
