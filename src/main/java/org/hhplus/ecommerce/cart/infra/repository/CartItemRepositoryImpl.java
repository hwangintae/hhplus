package org.hhplus.ecommerce.cart.infra.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.infra.jpa.CartItem;
import org.hhplus.ecommerce.cart.infra.jpa.CartItemJpaRepository;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {

    private final CartItemJpaRepository cartItemJpaRepository;

    @Override
    public CartItem findById(Long id) {
        return cartItemJpaRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException(EcommerceErrors.ITEM_NOT_FOUND.getMessage(), 1));
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemJpaRepository.findByCartId(cartId);
    }

    @Override
    public CartItem save(CartItem cartItem) {
        return cartItemJpaRepository.save(cartItem);
    }
}
