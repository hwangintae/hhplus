package org.hhplus.ecommerce.cart.infra.repository;

import org.hhplus.ecommerce.cart.infra.jpa.Cart;

public interface CartRepository {
    Cart findByUserId(Long userId);

    Cart findByOrElse(Long userId);
}
