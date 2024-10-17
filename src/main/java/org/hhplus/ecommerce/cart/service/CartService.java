package org.hhplus.ecommerce.cart.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.entity.Cart;
import org.hhplus.ecommerce.cart.entity.CartItem;
import org.hhplus.ecommerce.cart.entity.CartItemRepository;
import org.hhplus.ecommerce.cart.entity.CartRepository;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
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
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EcommerceBadRequestException(EcommerceErrors.USER_NOT_FOUND));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        return cartItems.stream()
                .map(CartItem::toDomain)
                .toList();
    }

    @Transactional
    public CartItemDomain addItem(Long userId, Long itemId, int cnt) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CartDomain cartDomain = CartDomain.generateCartDomain(userId);

                    return cartRepository.save(cartDomain.toEntity());
                });

        CartItemDomain cartItemDomain = CartItemDomain.generateCartItemDomain(cart.getId(), itemId, cnt);

        CartItem cartItem = cartItemRepository.save(cartItemDomain.toEntity());

        return cartItem.toDomain();
    }

    @Transactional
    public void deleteItem(Long cartItemId) {
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();

            CartItemDomain cartItemDomain = cartItem.toDomain();

            cartItemDomain.deleteCartItem();

            cartItemRepository.save(cartItemDomain.toEntity());
        }
    }
}
