package org.hhplus.ecommerce.item.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.item.service.ItemDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private long price;

    @Builder
    protected Item(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ItemDomain toDomain() {
        return ItemDomain.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .build();
    }
}
