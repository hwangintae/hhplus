package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint selectById(Long id) {

        return userPointTable.selectById(id);
    }

    @Override
    public UserPoint insertOrUpdate(Long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
