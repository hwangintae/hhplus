package org.hhplus.ecommerce.cart.usecase;

import org.hhplus.ecommerce.cart.infra.jpa.CartItemJpaRepository;
import org.hhplus.ecommerce.cart.infra.jpa.CartJpaRepository;
import org.hhplus.ecommerce.cart.service.CartItemCreate;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.infra.jpa.Item;
import org.hhplus.ecommerce.item.infra.jpa.ItemJpaRepository;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.jpa.StockJpaRepository;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class CartFacadeTest {

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
    private CartFacade cartFacade;

    @Autowired
    private CartJpaRepository cartJpaRepository;

    @Autowired
    private CartItemJpaRepository cartItemJpaRepository;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private StockJpaRepository stockJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        cartJpaRepository.deleteAll();
        cartItemJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
        stockJpaRepository.deleteAll();
        userRepository.deleteAll();
    }

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