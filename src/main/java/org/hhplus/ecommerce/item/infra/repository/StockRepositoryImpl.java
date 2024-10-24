package org.hhplus.ecommerce.item.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.jpa.StockJpaRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ITEM_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock findByItemId(Long itemId) {
        return stockJpaRepository.findByItemId(itemId)
                .orElseThrow(() -> new EmptyResultDataAccessException(ITEM_NOT_FOUND.getMessage(), 1));
    }

    @Override
    public List<Stock> findByItemIdIn(List<Long> itemIds) {
        return stockJpaRepository.findByItemIdIn(itemIds);
    }

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }
}
