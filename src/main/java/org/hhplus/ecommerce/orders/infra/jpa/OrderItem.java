package org.hhplus.ecommerce.orders.infra.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.common.BaseEntity;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ordersId;

    private Long itemId;

    private int itemCnt;

    private boolean deleteAt;

    @Builder
    protected OrderItem(Long id, Long ordersId, Long itemId, int itemCnt, boolean deleteAt) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.deleteAt = deleteAt;
    }

    public OrderItemDomain toDomain() {
        return OrderItemDomain.builder()
                .id(this.id)
                .ordersId(this.ordersId)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .deleteAt(this.deleteAt)
                .build();
    }
}
