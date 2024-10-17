package org.hhplus.ecommerce.item.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.item.service.HashTagDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder
    protected HashTag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public HashTagDomain toDomain() {
        return HashTagDomain.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
