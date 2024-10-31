package org.hhplus.ecommerce.cart.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.cart.service.CartItemCreate;
import org.hhplus.ecommerce.cart.service.CartItemDomain;
import org.hhplus.ecommerce.cart.service.CartService;
import org.hhplus.ecommerce.common.DistributedLock;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.service.ItemDomain;
import org.hhplus.ecommerce.item.service.ItemService;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.item.service.StockService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final ItemService itemService;
    private final StockService stockService;
    private final DistributedLock distributedLock;

    public CartItemDomain addCart(Long userId, CartItemCreate create) {
        Long itemId = create.getItemId();

        ItemDomain itemDomain = itemService.getItem(itemId);

        StockDomain stockDomain = distributedLock.redissonLock("stock:" + itemId, () -> stockService.getStock(itemId));

        if (stockDomain.checkQuantity(create.getCnt())) {
            return cartService.addItem(userId, itemDomain.getId(), create.getCnt());
        }

        throw new EcommerceBadRequestException(EcommerceErrors.INSUFFICIENT_STOCK_NOT_IN_CART);
    }

}
