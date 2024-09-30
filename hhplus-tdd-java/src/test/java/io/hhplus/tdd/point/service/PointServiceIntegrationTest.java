package io.hhplus.tdd.point.service;

import io.hhplus.tdd.entity.PointHistory;
import io.hhplus.tdd.entity.UserPoint;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Test
    @DisplayName("같은 id로 충전과 사용을 요청했을 때, 충전과 사용 금액이 같아하고 이력에 남아있어야 한다.")
    void test1() {
        // given
        long userId = 1111L;
        long currentPoint = 10_000_000L;
        long closedCount = 30L;

        pointService.charge(userId, currentPoint);

        //when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, closedCount)
                        .mapToObj(idx -> List.of(CompletableFuture.runAsync(() -> pointService.charge(userId, 100L)),
                                CompletableFuture.runAsync(() -> pointService.use(userId, 100L))))
                        .flatMap(List::stream)
                        .toArray(CompletableFuture[]::new)
        ).join();

        //then
        List<PointHistory> pointHistories = pointService.getPointHistory(userId);
        UserPoint userPoint = pointService.getUserPoint(userId);

        assertThat(pointHistories).hasSize(1 + 30 + 30);
        assertThat(pointHistories.stream().filter(pointHistory -> pointHistory.type().equals(TransactionType.CHARGE))).hasSize(31);
        assertThat(pointHistories.stream().filter(pointHistory -> pointHistory.type().equals(TransactionType.USE))).hasSize(30);
        assertThat(userPoint.point()).isEqualTo(currentPoint);
    }

    @Test
    @DisplayName("여러명이 동시에 포인트 충전 요청을 해도 충전된 포인트에는 누락이 없어야 한다.")
    void test2() {
        // given
        long updateMillis = System.currentTimeMillis();
        List<UserPoint> userPoints = List.of(
                new UserPoint(2L, 100L, updateMillis),
                new UserPoint(1L, 100L, updateMillis),
                new UserPoint(4L, 100L, updateMillis),
                new UserPoint(7L, 100L, updateMillis),
                new UserPoint(4L, 100L, updateMillis),
                new UserPoint(8L, 100L, updateMillis),
                new UserPoint(3L, 100L, updateMillis),
                new UserPoint(6L, 100L, updateMillis),
                new UserPoint(4L, 100L, updateMillis),
                new UserPoint(7L, 100L, updateMillis)
        );

        // when
        CompletableFuture.allOf(
                userPoints.stream()
                        .map(userPoint -> CompletableFuture.runAsync(() -> pointService.charge(userPoint.id(), userPoint.point())))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        assertThat(pointService.getUserPoint(2L).point()).isEqualTo(100);
        assertThat(pointService.getUserPoint(1L).point()).isEqualTo(100);
        assertThat(pointService.getUserPoint(4L).point()).isEqualTo(300);
        assertThat(pointService.getUserPoint(7L).point()).isEqualTo(200);
        assertThat(pointService.getUserPoint(8L).point()).isEqualTo(100);
        assertThat(pointService.getUserPoint(3L).point()).isEqualTo(100);
        assertThat(pointService.getUserPoint(6L).point()).isEqualTo(100);

    }

}
