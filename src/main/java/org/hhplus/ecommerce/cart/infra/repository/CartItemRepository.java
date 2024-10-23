package org.hhplus.ecommerce.cart.infra.repository;

import org.hhplus.ecommerce.cart.infra.jpa.CartItem;

import java.util.List;

public interface CartItemRepository {

    CartItem findById(Long id);

    List<CartItem> findByCartId(Long cartId);

    CartItem save(CartItem cartItem);
}
