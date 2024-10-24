package org.hhplus.ecommerce.cash.service;

import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.repository.CashRepository;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hhplus.ecommerce.common.exception.EcommerceErrors.ILLEGAL_AMOUNT;
import static org.hhplus.ecommerce.common.exception.EcommerceErrors.INSUFFICIENT_USER_CASH;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {
    @InjectMocks
    private CashService cashService;

    @Mock
    private CashRepository cashRepository;

    @Test
    @DisplayName("잔액 사용 시, 충전된 금액 보다 많은 금액을 사용할 수 없다.")
    public void subCashWithException() {
        // given
        Long userId = 1L;
        int amount = 10;

        CashRequest cashRequest = CashRequest.builder()
                .userId(userId)
                .amount(amount + 100)
                .build();

        Cash cash = Cash.builder()
                .id(22L)
                .userId(userId)
                .amount(amount)
                .build();

        given(cashRepository.findByUserId(anyLong())).willReturn(cash);

        // expected
        assertThatThrownBy(() -> cashService.subCash(cashRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(INSUFFICIENT_USER_CASH.getMessage());
    }

    @Test
    @DisplayName("잔액 충전 시, 0 또는 음수를 충전할 경우 exception이 발생한다.")
    public void notAddZeroAndNegativeAmount() {
        // given
        Long userId = 8L;

        Cash cash = Cash.builder()
                .id(111L)
                .userId(userId)
                .amount(1_000L)
                .build();

        CashRequest cashRequest = CashRequest.builder()
                .userId(userId)
                .amount(-1L)
                .build();

        given(cashRepository.findByUserId(anyLong())).willReturn(cash);

        // expected
        assertThatThrownBy(() -> cashService.addCash(cashRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ILLEGAL_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("잔액 사용 시, 0 또는 음수를 사용할 경우 exception이 발생한다.")
    public void notSubZeroAndNegativeAmount() {
        // given
        Long userId = 8L;

        Cash cash = Cash.builder()
                .id(111L)
                .userId(userId)
                .amount(1_000L)
                .build();

        CashRequest cashRequest = CashRequest.builder()
                .userId(userId)
                .amount(0L)
                .build();

        given(cashRepository.findByUserId(anyLong())).willReturn(cash);

        // expected
        assertThatThrownBy(() -> cashService.subCash(cashRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ILLEGAL_AMOUNT.getMessage());
    }

}