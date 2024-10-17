package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.entity.TopOrderItem;

import java.time.LocalDate;

@Getter
public class TopOrderItemDomain {

    private final Long id;
    private final LocalDate createDay;
    private final Long itemId;
    private final int cnt;

    @Builder
    protected TopOrderItemDomain(Long id, LocalDate createDay, Long itemId, int cnt) {
        this.id = id;
        this.createDay = createDay;
        this.itemId = itemId;
        this.cnt = cnt;
    }

    public TopOrderItem toEntity() {
        return TopOrderItem.builder()
                .id(this.id)
                .createDay(this.createDay)
                .itemId(this.itemId)
                .cnt(this.cnt)
                .build();
    }
}
