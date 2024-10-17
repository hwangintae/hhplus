package org.hhplus.ecommerce.cart.service;

import org.assertj.core.api.Assertions;
import org.hhplus.ecommerce.cart.entity.Cart;
import org.hhplus.ecommerce.cart.entity.CartItem;
import org.hhplus.ecommerce.cart.entity.CartItemRepository;
import org.hhplus.ecommerce.cart.entity.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("카트에 상품을 추가할 때 cart가 없는 경우 cart를 생성하고 상품을 추가한다.")
    public void addCartItem() {
        // given
        Long userId = 5134L;
        Long cartId = 444L;
        Long itemId = 322L;
        Long cartItemId = 77L;
        int cnt = 80;

        Cart cart = Cart.builder()
                .id(cartId)
                .userId(userId)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(cartItemId)
                .cartId(444L)
                .itemId(itemId)
                .cnt(cnt)
                .deleteAt(false)
                .build();

        given(cartRepository.findByUserId(anyLong())).willReturn(Optional.empty());
        given(cartRepository.save(any(Cart.class))).willReturn(cart);
        given(cartItemRepository.save(any(CartItem.class))).willReturn(cartItem);

        // when
        CartItemDomain cartItemDomain = cartService.addItem(userId, itemId, cnt);

        // then
        assertThat(cartItemDomain)
                .extracting("id", "cartId", "itemId", "cnt", "deleteAt")
                .contains(cartItemId, cartId, itemId, cnt, false);
    }

}