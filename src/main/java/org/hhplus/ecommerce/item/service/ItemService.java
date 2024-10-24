package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDomain getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .toDomain();
    }

    public List<ItemDomain> getItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);

        return items.stream()
                .map(Item::toDomain)
                .toList();
    }
}
