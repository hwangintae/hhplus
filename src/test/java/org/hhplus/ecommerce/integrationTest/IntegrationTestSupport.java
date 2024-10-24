package org.hhplus.ecommerce.integrationTest;

import org.hhplus.ecommerce.cash.entity.CashHistoryRepository;
import org.hhplus.ecommerce.cash.entity.CashRepository;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.item.entity.ItemRepository;
import org.hhplus.ecommerce.item.entity.StockRepository;
import org.hhplus.ecommerce.item.service.StockService;
import org.hhplus.ecommerce.orders.entity.OrderItemRepository;
import org.hhplus.ecommerce.orders.entity.OrdersRepository;
import org.hhplus.ecommerce.orders.usecase.OrdersFacade;
import org.hhplus.ecommerce.user.entity.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public abstract class IntegrationTestSupport {

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
    protected CashService cashService;

    @Autowired
    protected StockService stockService;

    @Autowired
    protected OrdersRepository ordersRepository;

    @Autowired
    protected OrderItemRepository orderItemRepository;

    @Autowired
    protected CashRepository cashRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected CashHistoryRepository cashHistoryRepository;

    @Autowired
    protected StockRepository stockRepository;

    @Autowired
    protected UserRepository userRepository;

    @AfterEach
    void tearDown() {
        ordersRepository.deleteAll();
        orderItemRepository.deleteAll();
        cashRepository.deleteAll();
        itemRepository.deleteAll();
        cashHistoryRepository.deleteAll();
        stockRepository.deleteAll();
        userRepository.deleteAll();
    }
}
