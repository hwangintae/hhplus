package org.hhplus.ecommerce.item.infra.repository;

import org.hhplus.ecommerce.item.infra.jpa.Stock;

import java.util.List;

public interface StockRepository {
    Stock findByItemId(Long itemId);
    List<Stock> findByItemIdIn(List<Long> itemIds);

    Stock save(Stock stock);
}
