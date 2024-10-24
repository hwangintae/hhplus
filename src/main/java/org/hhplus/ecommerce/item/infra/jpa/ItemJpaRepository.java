package org.hhplus.ecommerce.item.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findByIdIn(List<Long> itemIds);
}
