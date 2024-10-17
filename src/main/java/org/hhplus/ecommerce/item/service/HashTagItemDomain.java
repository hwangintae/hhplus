package org.hhplus.ecommerce.item.service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class HashTagItemDomain {

    private Long id;
    private Long hashTagId;
    private Long itemId;
    private boolean deleteAt;
}
