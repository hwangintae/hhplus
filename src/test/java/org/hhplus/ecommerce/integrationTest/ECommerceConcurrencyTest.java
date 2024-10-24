package org.hhplus.ecommerce.integrationTest;

import org.hhplus.ecommerce.cash.entity.Cash;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.item.entity.Item;
import org.hhplus.ecommerce.item.entity.Stock;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ECommerceConcurrencyTest extends IntegrationTestSupport {

    @Test
    @DisplayName("한명의 사용자가 동시에 여러번 주문 했을 경우 동시성 제어 테스트")
    public void createOrder() {
        // given
        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashRepository.save(Cash.builder()
                .userId(userId)
                .amount(Long.MAX_VALUE)
                .build());

        Item item = itemRepository.save(Item.builder().name("반포자이").price(2_900L).build());
        stockRepository.save(Stock.builder().itemId(item.getId()).quantity(100).build());


        List<OrderRequest> orderRequests = List.of(
                OrderRequest.builder().itemId(item.getId()).cnt(1).build()
        );

        // when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 30)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> ordersFacade.createOrder(userId, orderRequests)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        // 재고 확인
        StockDomain stockDomain = stockService.getStock(item.getId());
        assertThat(stockDomain.getQuantity()).isEqualTo(100 - 30);

        // 잔액이 맞는지 확인
        CashDomain cashDomain = cashService.getCash(userId);
        assertThat(cashDomain.getAmount()).isEqualTo(Long.MAX_VALUE - 2_900L * 30);
    }


    @Test
    @DisplayName("사용자가 동시에 잔액 충전을 요청했을 때 동시성 제어")
    public void addCash() {
        // given
        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashRepository.save(Cash.builder()
                .userId(userId)
                .amount(10_000L)
                .build());

        CashRequest cashRequest = CashRequest.builder().userId(userId).amount(100_000L).build();

        // when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 30)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> cashService.addCash(cashRequest)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        CashDomain cashDomain = cashService.getCash(userId);
        assertThat(cashDomain.getAmount()).isEqualTo(10_000L + 100_000L * 30);
    }


}
