package org.hhplus.ecommerce.orders.usecase;

import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.integrationTest.IntegrationTestSupport;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class OrdersFacadeTest extends IntegrationTestSupport {

    @Test
    @DisplayName("상품을 주문할 때 재고가 있는 상품만 주문 된다.")
    public void createOrder() {
        // given
        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(Long.MAX_VALUE)
                .build());

        itemJpaRepository.saveAll(List.of(
                Item.builder().name("신촌그랑자이").price(2_900L).build(),
                Item.builder().name("브라이튼N40").price(3_200L).build(),
                Item.builder().name("아크로서울포레스트").price(5_500L).build()
        ));
        List<Item> items = itemJpaRepository.findAll();

        List<Stock> stocks = items.stream()
                .map(item -> Stock.builder()
                        .itemId(item.getId())
                        .quantity(100)
                        .build())
                .toList();
        stockJpaRepository.saveAll(stocks);

        List<OrderRequest> orderRequests = List.of(
                OrderRequest.builder().itemId(items.get(0).getId()).cnt(99).build(),
                OrderRequest.builder().itemId(items.get(1).getId()).cnt(100).build(),
                OrderRequest.builder().itemId(items.get(2).getId()).cnt(101).build()
        );

        // when
        List<OrderItemDomain> orderItemDomains = ordersFacade.createOrder(userId, orderRequests);

        // then
        CashDomain cashDomain = cashService.getCash(userId);

        assertThat(orderItemDomains).hasSize(2)
                .extracting("itemId", "itemCnt")
                .contains(
                        tuple(items.get(0).getId(), 99),
                        tuple(items.get(1).getId(), 100)
                );

        // 잔액이 맞는지 확인
        assertThat(cashDomain.getAmount())
                .isEqualTo(Long.MAX_VALUE - (2_900L * 99) - (3_200L * 100));
    }

}