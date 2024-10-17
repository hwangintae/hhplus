package org.hhplus.ecommerce.cart.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.service.CartItemCreate;
import org.hhplus.ecommerce.cart.service.CartItemDomain;
import org.hhplus.ecommerce.cart.service.CartService;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.service.ItemDomain;
import org.hhplus.ecommerce.item.service.ItemService;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.item.service.StockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartUsecase {

    private final CartService cartService;
    private final ItemService itemService;
    private final StockService stockService;

    @Transactional
    public CartItemDomain addCart(Long userId, CartItemCreate create) {
        Long itemId = create.getItemId();

        ItemDomain itemDomain = itemService.getItem(itemId);
        StockDomain stockDomain = stockService.getStock(itemId);

        if (stockDomain.checkQuantity(create.getCnt())) {
            return cartService.addItem(userId, itemDomain.getId(), create.getCnt());
        }

        throw new EcommerceBadRequestException(EcommerceErrors.INSUFFICIENT_STOCK_NOT_IN_CART);
    }

}
