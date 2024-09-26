package io.hhplus.tdd.point.service;


import io.hhplus.tdd.entity.UserPoint;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        UserPoint userPoint = getUserPoint(id);

        UserPoint add = userPoint.add(amount);

        userPointRepository.insertOrUpdate(add.id(), add.point());
        pointHistoryRepository.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return add;
    }
}
