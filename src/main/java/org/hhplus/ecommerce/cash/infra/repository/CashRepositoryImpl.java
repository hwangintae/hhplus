package org.hhplus.ecommerce.cash.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashJpaRepository;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CashRepositoryImpl implements CashRepository {

    private final CashJpaRepository cashJpaRepository;

    @Override
    public Cash findByUserId(Long userId) {
        return cashJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException(EcommerceErrors.USER_NOT_FOUND.getMessage(), 1));
    }

    @Override
    public Cash save(Cash cash) {
        return cashJpaRepository.save(cash);
    }


}
