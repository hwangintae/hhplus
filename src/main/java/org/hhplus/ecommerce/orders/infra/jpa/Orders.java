package org.hhplus.ecommerce.orders.infra.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.service.OrdersDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Builder
    protected Orders(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public OrdersDomain toDomain() {
        return OrdersDomain.builder()
                .id(this.id)
                .userId(this.userId)
                .build();
    }
}
