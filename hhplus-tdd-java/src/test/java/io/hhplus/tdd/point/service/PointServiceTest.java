package io.hhplus.tdd.point.service;

import io.hhplus.tdd.entity.PointHistory;
import io.hhplus.tdd.entity.UserPoint;
import io.hhplus.tdd.exception.MaxChargeException;
import io.hhplus.tdd.exception.MinChargeException;
import io.hhplus.tdd.exception.MinusPointException;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserLockManager userLockManager;

    @Mock
    private Lock lock;

    @Test
    @DisplayName("사용자 id를 입력했을 때 포인트가 있는 사용자는 현재 보유 포인트가 출력된다.")
    void getUserPoint() {
        // given
        long userId = 1L;
        long updateMs = System.currentTimeMillis();

        UserPoint userPoint = new UserPoint(userId, 800L, updateMs);
        given(userPointRepository.selectById(any(Long.class)))
                .willReturn(userPoint);

        // when
        UserPoint result = pointService.getUserPoint(userId);

        // then
        assertThat(result).isEqualTo(new UserPoint(userId, 800L, updateMs));
    }

    @Test
    @DisplayName("포인트를 충전하면 충전 금액 만큼 충전된다.")
    void charge() {
        // given
        givenUserLock();
        long userId = 100L;

        UserPoint currentPoint = new UserPoint(userId, 1_000L, System.currentTimeMillis());
        UserPoint addPoint = new UserPoint(userId, 100_000L, System.currentTimeMillis());

        given(userPointRepository.selectById(any(Long.class))).willReturn(currentPoint);
        given(userPointRepository.insertOrUpdate(any(Long.class), any(Long.class))).willReturn(addPoint);

        // when
        UserPoint charged = pointService.charge(userId, addPoint.point());
        when(pointService.getUserPoint(any(Long.class))).thenReturn(charged);

        // then
        UserPoint resultPoint = pointService.getUserPoint(100L);
        assertThat(resultPoint.point())
                .isEqualTo(currentPoint.point() + addPoint.point());
    }

    @Test
    @DisplayName("포인트를 사용하면 사용 금액 만큼 차감된다.")
    void use() {
        // given
        givenUserLock();

        long userId = 100L;

        UserPoint currentPoint = new UserPoint(userId, 1_000L, System.currentTimeMillis());
        UserPoint subPoint = new UserPoint(userId, 1L, System.currentTimeMillis());

        given(userPointRepository.selectById(any(Long.class))).willReturn(currentPoint);
        given(userPointRepository.insertOrUpdate(any(Long.class), any(Long.class))).willReturn(subPoint);

        // when
        UserPoint used = pointService.use(userId, subPoint.point());
        when(pointService.getUserPoint(any(Long.class))).thenReturn(used);

        // then
        UserPoint resultPoint = pointService.getUserPoint(100L);
        assertThat(resultPoint.point())
                .isEqualTo(currentPoint.point() - subPoint.point());
    }

    @Test
    @DisplayName("포인트를 충전할 때, 최대 금액 50_000_000L을 넘어가면 MaxChargeException 이 발생한다.")
    void maxChargeException() {
        // given
        givenUserLock();

        long userId = 77L;
        given(userPointRepository.selectById(any(Long.class))).willReturn(new UserPoint(userId, 800L, System.currentTimeMillis()));

        // when // then
        assertThatThrownBy(() -> pointService.charge(userId, 50_000_000L))
                .isInstanceOf(MaxChargeException.class)
                .hasMessage("포인트는 최대 50,000,000(오천만)을 넘을 수 없습니다.");
    }

    @Test
    @DisplayName("포인트를 충전할 때, 0 이하의 포인트를 충전하면 MinChargeException 이 발생한다.")
    void minChargeException() {
        // given
        givenUserLock();

        long userId = 77L;
        given(userPointRepository.selectById(any(Long.class))).willReturn(new UserPoint(userId, 800L, System.currentTimeMillis()));

        // when // then
        assertThatThrownBy(() -> pointService.charge(userId, 0L))
                .isInstanceOf(MinChargeException.class)
                .hasMessage("1 이상의 포인트를 입력해 주세요.");

        assertThatThrownBy(() -> pointService.charge(userId, -100L))
                .isInstanceOf(MinChargeException.class)
                .hasMessage("1 이상의 포인트를 입력해 주세요.");
    }

    @Test
    @DisplayName("충전된 금액보다 더 많은 포인트를 사용하면 MinusPointException이 발생한다.")
    void minusPointException() {
        // given
        givenUserLock();

        long userId = 18L;
        given(userPointRepository.selectById(any(Long.class))).willReturn(new UserPoint(userId, 800L, System.currentTimeMillis()));

        // when // then
        assertThatThrownBy(() -> pointService.use(userId, 50_000_000L))
                .isInstanceOf(MinusPointException.class)
                .hasMessage("잔액이 모자랍니다.");
    }

    @Test
    @DisplayName("포인트 충전, 사용 이력이 있는 사용자의 경우 이력이 출력된다.")
    void getPointHistory() {
        // given
        long userId = 93L;

        List<PointHistory> pointHistories = LongStream.rangeClosed(1, 7)
                .mapToObj(cursor -> new PointHistory(cursor,
                        userId,
                        50_000L,
                        TransactionType.CHARGE,
                        System.currentTimeMillis()))
                .toList();

        given(pointHistoryRepository.selectAllByUserId(userId)).willReturn(pointHistories);

        // when
        List<PointHistory> pointHistory = pointService.getPointHistory(userId);

        // then
        assertThat(pointHistory).isEqualTo(pointHistories);
    }

    @Test
    @DisplayName("포인트를 충전하거나 사용한 적이 없는 사용자는 empty List 가 출력된다.")
    void getEmptyPointHistory() {
        // given
        long userId = 99L;

        given(pointHistoryRepository.selectAllByUserId(any(Long.class))).willReturn(List.of());

        // when
        List<PointHistory> pointHistory = pointService.getPointHistory(userId);

        // then
        assertThat(pointHistory).isEmpty();
    }

    // mocking lock 을 추가하면 안되는 테스트가 있어 따로 뺐음
    private void givenUserLock() {
        given(userLockManager.getUserLock(any(Long.class))).willReturn(lock);
        // Mock the lock to simulate successful lock acquisition
        try {
            given(lock.tryLock(any(Long.class), any(TimeUnit.class))).willReturn(true);
        } catch (Exception ignore) {}
    }
}