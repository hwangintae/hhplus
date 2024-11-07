package org.hhplus.ecommerce.item.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.item.service.ItemDomain;
import org.hhplus.ecommerce.item.service.ItemService;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.item.service.StockService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class ItemFacade {

    private final ItemService itemService;
    private final StockService stockService;

    public ItemResponse getItemWithStock(Long itemId) {

        ItemDomain itemDomain = itemService.getItem(itemId);
        StockDomain stockDomain = stockService.getStock(itemId);

        return ItemResponse.of(itemDomain, stockDomain);
    }

    // 상품목록 조회
    public List<ItemResponse> getItemsWithStock(List<Long> itemIds) {

        List<ItemDomain> itemDomains = itemService.getItems(itemIds);

        List<StockDomain> stockDomains = new ArrayList<>();
        for (Long itemId : itemIds) {
            StockDomain stockDomain = stockService.getStock(itemId);

            stockDomains.add(stockDomain);
        }

        Map<Long, StockDomain> stockDomainMap = stockDomains.stream()
                .collect(toMap(StockDomain::getItemId, stockDomain -> stockDomain));

        return itemDomains.stream()
                .map(itemDomain -> ItemResponse.of(itemDomain, stockDomainMap.get(itemDomain.getId())))
                .toList();
    }
}
