package org.hhplus.ecommerce.cart.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CarItemDeleteRequest {
    private Long userId;
    private Long cartItemId;
}
