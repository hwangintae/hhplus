package org.hhplus.ecommerce.item.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.ItemJpaRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ITEM_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public Item findById(Long itemId) {
        return itemJpaRepository.findById(itemId)
                .orElseThrow(() -> new EmptyResultDataAccessException(ITEM_NOT_FOUND.getMessage(), 1));
    }

    @Override
    public List<Item> findByIdIn(List<Long> itemIds) {
        return itemJpaRepository.findByIdIn(itemIds);
    }
}
