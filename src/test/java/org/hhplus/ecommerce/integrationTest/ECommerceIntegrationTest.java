package org.hhplus.ecommerce.integrationTest;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersOutbox;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class ECommerceIntegrationTest extends IntegrationTestSupport {

    @Test
    @DisplayName("사용자가 동시에 잔액 충전을 요청했을 때 동시성 제어")
    public void addCash() {
        // given
        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(10_000L)
                .build());

        CashRequest cashRequest = CashRequest.builder().userId(userId).amount(100_000L).build();

        // when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 30)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> cashFacade.addCash(cashRequest)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        CashDomain cashDomain = cashService.getCash(userId);
        assertThat(cashDomain.getAmount()).isEqualTo(10_000L + 100_000L * 30);
    }

    @Test
    @DisplayName("DistributedLock Aop를 이용한 락 획득")
    public void distributeAop() {
        // given
        User user = userRepository.save(User.builder().username("이석범").build());
        Long userId = user.getId();
        long initAmount = 1_000_000_000L;
        long addAmount = 100_000_000L;

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(initAmount)
                .build());

        CashRequest cashRequest = CashRequest.builder()
                .userId(userId)
                .amount(addAmount)
                .build();

        // when
        CashDomain cashDomain = cashFacade.addCash(cashRequest);

        // then
        assertThat(cashDomain.getAmount()).isEqualTo(initAmount + addAmount);
    }
    
    @Test
    @DisplayName("재고 차감 시 MultiDistributedLock을 사용해서 각각의 분산락을 하나의 분산락으로 처리하여 진행한다.")
    public void stockMultiDistributedLockTest() {
        // given
        User user = userRepository.save(User.builder().username("이석범").build());
        Long userId = user.getId();
        long initAmount = 1_000_000_000L;

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(initAmount)
                .build());

        Long itemPrice1 = 2_900L;
        Long itemPrice2 = 3_200L;
        Long itemPrice3 = 5_500L;

        itemJpaRepository.saveAll(List.of(
                Item.builder().name("신촌그랑자이").price(itemPrice1).build(),
                Item.builder().name("브라이튼N40").price(itemPrice2).build(),
                Item.builder().name("아크로서울포레스트").price(itemPrice3).build()
        ));
        List<Item> items = itemJpaRepository.findAll();

        List<Stock> stocks = items.stream()
                .map(item -> Stock.builder()
                        .itemId(item.getId())
                        .quantity(100)
                        .build())
                .toList();
        stockJpaRepository.saveAll(stocks);

        List<OrderItemInfo> orderItemInfos = List.of(
                OrderItemInfo.builder().itemId(items.get(0).getId()).itemCnt(1).build(),
                OrderItemInfo.builder().itemId(items.get(1).getId()).itemCnt(1).build(),
                OrderItemInfo.builder().itemId(items.get(2).getId()).itemCnt(1).build()
        );

        // when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 30)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> itemFacade.subStockItem(userId, idx, orderItemInfos)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        List<StockDomain> stockDomains = stockService.getStocks(items.stream().map(Item::getId).toList());
        Assertions.assertThat(stockDomains)
                .extracting("itemId", "quantity")
                .contains(
                        items.stream()
                                .map(item -> tuple(item.getId(), 70))
                                .toArray(Tuple[]::new)
                );
    }

}
