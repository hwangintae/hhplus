package org.hhplus.ecommerce.orders.usecase;

import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashJpaRepository;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.integrationTest.IntegrationTestSupport;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.ItemJpaRepository;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.jpa.StockJpaRepository;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.user.entity.User;
import org.hhplus.ecommerce.user.entity.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ActiveProfiles("test")
class OrdersFacadeTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashJpaRepository cashJpaRepository;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;


    @Test
    @DisplayName("테스트 데이터 넣기")
    public void createOrder() {
        // given
        int OrderRequestCnt1 = 1;
        int OrderRequestCnt2 = 1;
        int OrderRequestCnt3 = 1;

        Long itemPrice1 = 2_900L;
        Long itemPrice2 = 3_200L;
        Long itemPrice3 = 5_500L;

        User user = userRepository.save(User.builder().username("타일러").build());
        Long userId = user.getId();

        cashJpaRepository.save(Cash.builder()
                .userId(userId)
                .amount(Long.MAX_VALUE)
                .build());

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

        List<OrderRequest> orderRequests = List.of(
                OrderRequest.builder().itemId(items.get(0).getId()).cnt(OrderRequestCnt1).build(),
                OrderRequest.builder().itemId(items.get(1).getId()).cnt(OrderRequestCnt2).build(),
                OrderRequest.builder().itemId(items.get(2).getId()).cnt(OrderRequestCnt3).build()
        );
    }

}