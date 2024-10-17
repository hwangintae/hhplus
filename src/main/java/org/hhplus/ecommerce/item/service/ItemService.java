package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.item.entity.Item;
import org.hhplus.ecommerce.item.entity.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemDomain getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EcommerceBadRequestException(ITEM_NOT_FOUND))
                .toDomain();
    }

    public List<ItemDomain> getItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);

        return items.stream()
                .map(Item::toDomain)
                .toList();
    }
}
