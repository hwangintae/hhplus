package org.hhplus.ecommerce.orders.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ordersId;

    private Long itemId;

    private int itemCnt;

    @Builder
    protected OrderItem(Long id, Long ordersId, Long itemId, int itemCnt) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }

    public static OrderItem of(OrderItemDomain domain) {
        return OrderItem.builder()
                .id(domain.getId())
                .ordersId(domain.getOrdersId())
                .itemId(domain.getItemId())
                .itemCnt(domain.getItemCnt())
                .build();
    }

    public OrderItemDomain toDomain() {
        return OrderItemDomain.builder()
                .id(this.id)
                .ordersId(this.ordersId)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .build();
    }
}
