package org.hhplus.ecommerce.integrationTest;

import org.hhplus.ecommerce.cart.infra.jpa.CartItemJpaRepository;
import org.hhplus.ecommerce.cart.infra.jpa.CartJpaRepository;
import org.hhplus.ecommerce.cart.usecase.CartFacade;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistoryJpaRepository;
import org.hhplus.ecommerce.cash.infra.jpa.CashJpaRepository;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.cash.usecase.CashFacade;
import org.hhplus.ecommerce.item.infra.jpa.ItemJpaRepository;
import org.hhplus.ecommerce.item.infra.jpa.StockJpaRepository;
import org.hhplus.ecommerce.item.service.StockService;
import org.hhplus.ecommerce.item.usecase.ItemFacade;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItemJpaRepository;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersJpaRepository;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersOutboxJpaRepository;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.hhplus.ecommerce.user.entity.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");
    public static RedisContainer redisContainer = new RedisContainer("redis:latest");
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.1.13"));

    static {
        mysqlContainer.start();
        redisContainer.start();
        kafkaContainer.start();
    }

    @DynamicPropertySource
    static void setUpProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getRedisPort);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
    }

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

    @Autowired
    protected CashFacade cashFacade;

    @Autowired
    protected OrdersOutboxJpaRepository ordersOutboxJpaRepository;

    @Autowired
    protected ItemFacade itemFacade;

    @AfterEach
    void tearDown() {
        ordersJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
        cashHistoryJpaRepository.deleteAll();
        stockJpaRepository.deleteAll();
        userRepository.deleteAll();
        cartJpaRepository.deleteAll();
        cartItemJpaRepository.deleteAll();
        ordersOutboxJpaRepository.deleteAll();
    }
}
