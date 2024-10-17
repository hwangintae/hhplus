package org.hhplus.ecommerce.cash.service;

import org.hhplus.ecommerce.cash.entity.Cash;
import org.hhplus.ecommerce.cash.entity.CashRepository;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hhplus.ecommerce.common.exception.EcommerceErrors.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {
    @InjectMocks
    private CashService cashService;

    @Mock
    private CashRepository cashRepository;

    @Test
    @DisplayName("잔액조회 시, 사용자 ID가 없는 경우 exception이 발생한다.")
    public void getCashWithoutUserId() {
        // given
        Long userId = 333L;

        given(cashRepository.findByUserId(anyLong())).willReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> cashService.getCash(userId))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.USER_NOT_FOUND.getMessage());
    }

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

        given(cashRepository.findByUserId(anyLong())).willReturn(Optional.of(cash));

        // expected
        assertThatThrownBy(() -> cashService.subCash(cashRequest))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.INSUFFICIENT_USER_CASH.getMessage());
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

        given(cashRepository.findByUserId(anyLong())).willReturn(Optional.of(cash));

        // expected
        assertThatThrownBy(() -> cashService.addCash(cashRequest))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.ILLEGAL_AMOUNT.getMessage());
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

        given(cashRepository.findByUserId(anyLong())).willReturn(Optional.of(cash));

        // expected
        assertThatThrownBy(() -> cashService.subCash(cashRequest))
                .isInstanceOf(EcommerceBadRequestException.class)
                .hasMessage(EcommerceErrors.ILLEGAL_AMOUNT.getMessage());
    }

}