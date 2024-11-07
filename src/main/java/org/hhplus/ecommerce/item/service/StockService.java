package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.redis.DistributedLock;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public StockDomain getStock(Long itemId) {
        return stockRepository.findByItemId(itemId)
                .toDomain();
    }

    @Transactional
    public List<StockDomain> getStocks(List<Long> itemIds) {
        List<Stock> stocks = stockRepository.findByItemIdIn(itemIds);

        return stocks.stream()
                .map(Stock::toDomain)
                .toList();
    }

    @DistributedLock(prefix = "stock", key = "#itemId")
    @Transactional
    public StockDomain subStock(Long itemId, int cnt) {
        StockDomain stockDomain = getStock(itemId);

        stockDomain.subQuantity(cnt);

        return stockRepository.save(stockDomain.toEntity())
                .toDomain();
    }

    public boolean checkStock(Long itemId, int cnt) {
        StockDomain stockDomain = getStock(itemId);

        return stockDomain.checkQuantity(cnt);
    }
}
