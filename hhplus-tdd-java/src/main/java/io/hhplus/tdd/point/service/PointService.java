package io.hhplus.tdd.point.service;

import io.hhplus.tdd.entity.PointHistory;
import io.hhplus.tdd.entity.UserPoint;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserLockManager userLockManager;

    public UserPoint getUserPoint(Long id) {
        return userPointRepository.selectById(id);
    }

    public UserPoint charge(Long id, long amount) {

        Lock lock = userLockManager.getUserLock(id);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(5L, TimeUnit.SECONDS);
            if(!acquired) {
                log.error("사용자 락 획특 타임아웃");
                throw new IllegalArgumentException("사용자 락 획득 타임아웃");
            }

            UserPoint userPoint = getUserPoint(id);

            UserPoint add = userPoint.add(amount);

            userPointRepository.insertOrUpdate(add.id(), add.point());
            pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return add;
        } catch (InterruptedException e) {
            log.error("사용자 락 획특 오류");
            throw new IllegalArgumentException("사용자 락 획득 오류");
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public UserPoint use(Long id, long amount) {

        Lock lock = userLockManager.getUserLock(id);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(5L, TimeUnit.SECONDS);
            if(!acquired) {
                log.error("사용자 락 획특 타임아웃");
                throw new IllegalArgumentException("사용자 락 획득 타임아웃");
            }
            UserPoint userPoint = getUserPoint(id);

            UserPoint sub = userPoint.sub(amount);

            userPointRepository.insertOrUpdate(sub.id(), sub.point());
            pointHistoryRepository.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

            return sub;
        } catch (InterruptedException e) {
            log.error("사용자 락 획특 오류");
            throw new IllegalArgumentException("사용자 락 획득 오류");
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public List<PointHistory> getPointHistory(Long id) {
        return pointHistoryRepository.selectAllByUserId(id);
    }
}
