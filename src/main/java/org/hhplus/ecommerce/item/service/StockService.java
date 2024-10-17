package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.item.entity.Stock;
import org.hhplus.ecommerce.item.entity.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ITEM_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    public StockDomain getStock(Long itemId) {
        return stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new EcommerceBadRequestException(ITEM_NOT_FOUND))
                .toDomain();
    }

    public List<StockDomain> getStocks(List<Long> itemIds) {
        List<Stock> stocks = stockRepository.findByItemIdIn(itemIds);

        return stocks.stream()
                .map(Stock::toDomain)
                .toList();
    }

    @Transactional
    public void subStock(Long itemId, int cnt) {
        StockDomain stockDomain = getStock(itemId);

        stockDomain.subQuantity(cnt);

        stockRepository.save(stockDomain.toEntity());
    }

    public boolean checkStock(Long itemId, int cnt) {
        StockDomain stockDomain = getStock(itemId);

        return stockDomain.checkQuantity(cnt);
    }
}
