package io.hhplus.tdd.point.repository;


import io.hhplus.tdd.entity.UserPoint;

public interface UserPointRepository {
    UserPoint selectById(Long id);
    UserPoint insertOrUpdate(Long id, long amount);
}
