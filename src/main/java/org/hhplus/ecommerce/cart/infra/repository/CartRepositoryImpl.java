package org.hhplus.ecommerce.cart.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.infra.jpa.Cart;
import org.hhplus.ecommerce.cart.infra.jpa.CartJpaRepository;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart findByUserId(Long userId) {

        return cartJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new EmptyResultDataAccessException(EcommerceErrors.USER_NOT_FOUND.getMessage(), 1));
    }

    @Override
    public Cart findByOrElse(Long userId) {

        return cartJpaRepository.findByUserId(userId)
                .orElseGet(() -> cartJpaRepository.save(Cart.of(userId)));
    }
}
