package org.hhplus.ecommerce.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedissonClient redissonClient;

    public <T> T redissonLock(String lockName, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean isLock = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLock) {
                log.info("Lock 획득 실패: {}", lockName);
                throw new RuntimeException("락 획득에 실패했습니다. : " + lockName);
            }

            log.info("락을 획득했습니다 : {}", lockName);
            // 작업 실행
            return task.get();

        } catch (InterruptedException e) {
            throw new RuntimeException("RLock InterruptedException : " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("RLock Exception : " + e.getMessage());
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락을 해제했습니다. : {}", lockName);
            }
        }
    }
}
