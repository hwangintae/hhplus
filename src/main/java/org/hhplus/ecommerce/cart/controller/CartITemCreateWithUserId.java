package org.hhplus.ecommerce.cart.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.cart.service.CartItemCreate;

@Getter
@NoArgsConstructor
public class CartITemCreateWithUserId {
    private Long userId;
    private CartItemCreate cartItemCreate;
}
