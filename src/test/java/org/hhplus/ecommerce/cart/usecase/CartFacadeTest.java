package org.hhplus.ecommerce.cart.usecase;

import org.hhplus.ecommerce.cart.service.CartItemCreate;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.integrationTest.IntegrationTestSupport;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartFacadeTest extends IntegrationTestSupport {

    @Test
    @DisplayName("재고가 부족한 상품은 장바구니에 담으면 exception이 발생한다.")
    public void addCartEmptyStock() {
        // given
        User user = User.builder()
                .username("타일러")
                .build();

        User saveUser = userRepository.save(user);

        Item item = Item.builder()
                .name("신촌그랑자이")
                .price(2_900L)
                .build();

        Item saveItem = itemJpaRepository.save(item);

        Stock stock = Stock.builder()
                .itemId(saveItem.getId())
                .quantity(2)
                .build();

        stockJpaRepository.save(stock);

        CartItemCreate cartItemCreate = CartItemCreate.builder()
                .itemId(saveItem.getId())
                .cnt(3)
                .build();

        // expected
        assertThatThrownBy(() -> cartFacade.addCart(saveUser.getId(), cartItemCreate))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.INSUFFICIENT_STOCK_NOT_IN_CART.getMessage());
    }

}