package org.hhplus.ecommerce.integrationTest;

import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.PopularItemsResult;
import org.hhplus.ecommerce.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class ECommerceIntegrationTest extends IntegrationTestSupport {

    @Test
    @DisplayName("한명의 사용자가 동시에 여러번 주문 했을 경우 동시성 제어 테스트")
    public void createOrder() {
        // given
        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(Long.MAX_VALUE)
                .build());

        Item item = itemJpaRepository.save(Item.builder().name("반포자이").price(2_900L).build());
        stockJpaRepository.save(Stock.builder().itemId(item.getId()).quantity(100).build());


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
    @DisplayName("최근 3일 간 가장 많이 팔린 상품 5개 찾기")
    @Sql(scripts = "/OrderItemTestDataSet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getPopularItems3DaysLimit5() {
        // given
        int from = 3;
        int limit = 5;

        // when
        List<PopularItemsResult> popularItems = ordersService.getPopularItems(from, limit);
        // then
        assertThat(popularItems)
                .extracting("itemId", "totalCnt")
                .contains(
                        tuple(100L, 50),
                        tuple(101L, 25)
                );
    }

    @Test
    @DisplayName("사용자 a는 1번, 2번, 3번, 4번, 5번 상품 구매, 사용자 b는 5번, 4번, 3번, 2번, 1번 구매 했을 때 동시성 제어")
    public void userABuy1to5AndUserBBuy5to1() {
        // given
        Long userIdA = 10L;
        Long userIdB = 11L;

        Long userAmount = 1_000_000_000L;
        int itemPrice = 100;
        int quantity = 3_000;


        cashJpaRepository.save(Cash.builder().userId(userIdA).amount(userAmount).build());
        cashJpaRepository.save(Cash.builder().userId(userIdB).amount(userAmount).build());

        Item item1 = itemJpaRepository.save(Item.builder().price(itemPrice).build());
        Item item2 = itemJpaRepository.save(Item.builder().price(itemPrice).build());
        Item item3 = itemJpaRepository.save(Item.builder().price(itemPrice).build());
        Item item4 = itemJpaRepository.save(Item.builder().price(itemPrice).build());
        Item item5 = itemJpaRepository.save(Item.builder().price(itemPrice).build());

        stockJpaRepository.save(Stock.builder().itemId(item1.getId()).quantity(quantity).build());
        stockJpaRepository.save(Stock.builder().itemId(item2.getId()).quantity(quantity).build());
        stockJpaRepository.save(Stock.builder().itemId(item3.getId()).quantity(quantity).build());
        stockJpaRepository.save(Stock.builder().itemId(item4.getId()).quantity(quantity).build());
        stockJpaRepository.save(Stock.builder().itemId(item5.getId()).quantity(quantity).build());

        List<OrderRequest> userAOrderRequests = List.of(
                OrderRequest.builder().itemId(item1.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item2.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item3.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item4.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item5.getId()).cnt(1).build()
        );

        List<OrderRequest> userBOrderRequests = List.of(
                OrderRequest.builder().itemId(item5.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item4.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item3.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item2.getId()).cnt(1).build(),
                OrderRequest.builder().itemId(item1.getId()).cnt(1).build()
        );

        // when
        CompletableFuture.allOf(
                IntStream.rangeClosed(1, 100)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> {
                                    ordersFacade.createOrder(userIdA, userAOrderRequests);
                                    ordersFacade.createOrder(userIdB, userBOrderRequests);
                                })
                        ).toArray(CompletableFuture[]::new)
        ).join();

        // then
        // 재고 차감 확인
        assertThat(stockService.getStock(item1.getId()).getQuantity()).isEqualTo(quantity - 200);
        assertThat(stockService.getStock(item2.getId()).getQuantity()).isEqualTo(quantity - 200);
        assertThat(stockService.getStock(item3.getId()).getQuantity()).isEqualTo(quantity - 200);
        assertThat(stockService.getStock(item4.getId()).getQuantity()).isEqualTo(quantity - 200);
        assertThat(stockService.getStock(item5.getId()).getQuantity()).isEqualTo(quantity - 200);

        // 사용자 금액 확인
        assertThat(cashService.getCash(userIdA).getAmount()).isEqualTo(userAmount - (itemPrice * 5 * itemPrice));
        assertThat(cashService.getCash(userIdB).getAmount()).isEqualTo(userAmount - (itemPrice * 5 * itemPrice));

        // 사용자 주문 확인
        List<OrderItemDomain> userAOrders = ordersService.getOrders(userIdA);

        Integer userAItemCnt = userAOrders.stream().map(OrderItemDomain::getItemCnt).reduce(0, Integer::sum);
        Integer userBItemCnt = userAOrders.stream().map(OrderItemDomain::getItemCnt).reduce(0, Integer::sum);

        assertThat(userAItemCnt).isEqualTo(500);
        assertThat(userBItemCnt).isEqualTo(500);
    }


}
