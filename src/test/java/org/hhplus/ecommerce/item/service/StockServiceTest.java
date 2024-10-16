package org.hhplus.ecommerce.item.service;

import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.entity.Stock;
import org.hhplus.ecommerce.item.entity.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    
    @InjectMocks
    private StockService stockService;
    
    @Mock
    private StockRepository stockRepository;
    
    @Test
    @DisplayName("재고 조회 시, 상품 ID가 없는 경우 exception이 발생한다.")
    public void getStockWithoutItemId() {
        // given
        Long itemId = 11L;

        given(stockRepository.findByItemId(anyLong())).willReturn(Optional.empty());
        
        // expected
        assertThatThrownBy(() -> stockService.getStock(itemId))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고 차감 시, 남은 수량 보다 많은 재고를 차감하면 exception이 발생한다.")
    public void subStockException() {
        // given
        Long itemId = 11L;
        int quantity = 10;

        Stock stock = Stock.builder()
                .id(111L)
                .itemId(itemId)
                .quantity(quantity)
                .build();

        given(stockRepository.findByItemId(anyLong())).willReturn(Optional.of(stock));

        // expected
        assertThatThrownBy(() -> stockService.subStock(itemId, quantity + 1))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.INSUFFICIENT_STOCK.getMessage());

    }

}