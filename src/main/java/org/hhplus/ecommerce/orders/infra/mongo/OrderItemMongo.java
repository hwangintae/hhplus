package org.hhplus.ecommerce.orders.infra.mongo;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Document(collection = "order_item")
public class OrderItemMongo {

    @Id
    private final Long id;
    private final Long itemId;
    private final int itemCnt;
    private final LocalDate orderedAt;

    @Builder
    protected OrderItemMongo(Long id, Long itemId, int itemCnt, LocalDate orderedAt) {
        this.id = id;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.orderedAt = orderedAt;
    }
}
