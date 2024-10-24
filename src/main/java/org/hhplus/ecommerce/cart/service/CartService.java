package org.hhplus.ecommerce.cart.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.infra.jpa.Cart;
import org.hhplus.ecommerce.cart.infra.jpa.CartItem;
import org.hhplus.ecommerce.cart.infra.repository.CartItemRepository;
import org.hhplus.ecommerce.cart.infra.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public List<CartItemDomain> getCartItems(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        return cartItems.stream()
                .map(CartItem::toDomain)
                .toList();
    }

    @Transactional
    public CartItemDomain addItem(Long userId, Long itemId, int cnt) {

        Cart cart = cartRepository.findByOrElse(userId);

        CartItemDomain cartItemDomain = CartItemDomain.generateCartItemDomain(cart.getId(), itemId, cnt);

        CartItem cartItem = cartItemRepository.save(cartItemDomain.toEntity());

        return cartItem.toDomain();
    }

    @Transactional
    public void deleteItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId);

        CartItemDomain cartItemDomain = cartItem.toDomain();

        cartItemDomain.deleteCartItem();

        cartItemRepository.save(cartItemDomain.toEntity());
    }
}
