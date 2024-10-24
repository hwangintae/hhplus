package org.hhplus.ecommerce.integrationTest;

import org.assertj.core.api.Assertions;
import org.hhplus.ecommerce.cart.infra.jpa.CartItemJpaRepository;
import org.hhplus.ecommerce.cart.infra.jpa.CartJpaRepository;
import org.hhplus.ecommerce.cart.usecase.CartFacade;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistoryJpaRepository;
import org.hhplus.ecommerce.cash.infra.jpa.CashJpaRepository;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.ItemJpaRepository;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.jpa.StockJpaRepository;
import org.hhplus.ecommerce.item.service.StockDomain;
import org.hhplus.ecommerce.item.service.StockService;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItemJpaRepository;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersJpaRepository;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.hhplus.ecommerce.orders.service.PopularItemsResult;
import org.hhplus.ecommerce.orders.usecase.OrdersFacade;
import org.hhplus.ecommerce.user.entity.User;
import org.hhplus.ecommerce.user.entity.UserRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@Testcontainers
public class ECommerceIntegrationTest {

    @ClassRule
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("db_commerce_srv")
            .withUsername("us_hhplus_commerce")
            .withPassword("commerce!#24");

    @DynamicPropertySource
    static void setUpProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    protected OrdersFacade ordersFacade;

    @Autowired
    protected OrdersService ordersService;

    @Autowired
    protected CashService cashService;

    @Autowired
    protected StockService stockService;

    @Autowired
    protected OrdersJpaRepository ordersJpaRepository;

    @Autowired
    protected OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    protected CashJpaRepository cashJpaRepository;

    @Autowired
    protected ItemJpaRepository itemJpaRepository;

    @Autowired
    protected CashHistoryJpaRepository cashHistoryJpaRepository;

    @Autowired
    protected StockJpaRepository stockJpaRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CartFacade cartFacade;

    @Autowired
    protected CartJpaRepository cartJpaRepository;

    @Autowired
    protected CartItemJpaRepository cartItemJpaRepository;

    @AfterEach
    void tearDown() {
        ordersJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        cashJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
        cashHistoryJpaRepository.deleteAll();
        stockJpaRepository.deleteAll();
        userRepository.deleteAll();
        cartJpaRepository.deleteAll();
        cartItemJpaRepository.deleteAll();
    }

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
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> cashService.addCash(cashRequest)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        CashDomain cashDomain = cashService.getCash(userId);
        assertThat(cashDomain.getAmount()).isEqualTo(10_000L + 100_000L * 30);
    }


    @Test
    @DisplayName("최근 3일 간 가장 많이 팔린 상품 5개 찾기")
    @Sql({"OrderItemTestDataSet.sql"})
    public void getPopularItems3DaysLimit5() {
        // given
        int from = 3;
        int limit = 5;

        // when
        List<PopularItemsResult> popularItems = ordersService.getPopularItems(from, limit);
        // then
        Assertions.assertThat(popularItems)
                .extracting("itemId", "totalCnt")
                .contains(
                        tuple(100L, 50),
                        tuple(101L, 25)
                );
    }


}
