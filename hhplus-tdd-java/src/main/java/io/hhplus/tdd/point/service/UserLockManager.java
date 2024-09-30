package io.hhplus.tdd.point.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class UserLockManager {
    private final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();

    public Lock getUserLock(Long id) {
        return locks.computeIfAbsent(id, key -> new ReentrantLock(true));
    }
}