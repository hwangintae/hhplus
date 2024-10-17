package org.hhplus.ecommerce.item.service;

import org.assertj.core.api.Assertions;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.item.entity.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 조회 시, 상품 ID가 없으면 exception이 발생한다.")
    public void getItemWithoutItemId() {
        // given
        Long itemId = 91L;

        given(itemRepository.findById(itemId)).willReturn(Optional.empty());

        // expected
        Assertions.assertThatThrownBy(() -> itemService.getItem(itemId))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.ITEM_NOT_FOUND.getMessage());

    }
}