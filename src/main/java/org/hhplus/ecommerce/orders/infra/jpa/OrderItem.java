package org.hhplus.ecommerce.orders.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.common.BaseEntity;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderStatus;

import java.time.LocalDate;

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

    private LocalDate orderedAt;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @Builder
    protected OrderItem(Long id, Long ordersId, Long itemId, int itemCnt, boolean deleteAt, LocalDate orderedAt, OrderStatus status) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.deleteAt = deleteAt;
        this.orderedAt = orderedAt;
        this.status = status;
    }

    public OrderItemDomain toDomain() {
        return OrderItemDomain.builder()
                .id(this.id)
                .ordersId(this.ordersId)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .deleteAt(this.deleteAt)
                .orderedAt(this.orderedAt)
                .status(this.status)
                .build();
    }
}
