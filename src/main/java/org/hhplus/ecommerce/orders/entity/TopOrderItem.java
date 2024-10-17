package org.hhplus.ecommerce.orders.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.service.TopOrderItemDomain;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate createDay;

    private Long itemId;

    private int cnt;

    @Builder
    protected TopOrderItem(Long id, LocalDate createDay, Long itemId, int cnt) {
        this.id = id;
        this.createDay = createDay;
        this.itemId = itemId;
        this.cnt = cnt;
    }

    public TopOrderItemDomain toDomain() {
        return TopOrderItemDomain.builder()
                .id(this.id)
                .createDay(this.createDay)
                .itemId(this.itemId)
                .cnt(this.cnt)
                .build();
    }
}
