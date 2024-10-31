package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.item.infra.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestoreStockService {

    private final StockRepository stockRepository;

    @Transactional
    public StockDomain addStock(Long itemId, int cnt) {
        StockDomain stockDomain = stockRepository.findByItemId(itemId)
                .toDomain();

        stockDomain.addQuantity(cnt);

        return stockRepository.save(stockDomain.toEntity())
                .toDomain();
    }
}
