package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.item.entity.HashTag;

@Getter
public class HashTagDomain {
    private final Long id;
    private final String name;


    @Builder
    protected HashTagDomain(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public HashTag toEntity() {
        return HashTag.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
