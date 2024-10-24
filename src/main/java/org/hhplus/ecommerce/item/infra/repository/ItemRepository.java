package org.hhplus.ecommerce.item.infra.repository;

import org.hhplus.ecommerce.item.infra.jpa.Item;

import java.util.List;

public interface ItemRepository {
    Item findById(Long itemId);
    List<Item> findByIdIn(List<Long> itemIds);
}
