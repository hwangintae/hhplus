package org.hhplus.ecommerce.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

    private final RedissonClient redissonClient;

    @Around("@annotation(org.hhplus.ecommerce.common.redis.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        RLock[] RLocks = Arrays.stream(key.split(","))
                .map(item -> redissonClient.getLock(String.join("", distributedLock.prefix(), ":", item)))
                .toArray(RLock[]::new);

        RLock multiLock = redissonClient.getMultiLock(RLocks);

        try {
            boolean available = multiLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                return false;
            }

            log.info(">>> MultiDistributedLock redis lock start : {}", key);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new RuntimeException("RLock InterruptedException : " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("RLock Exception : " + e.getMessage());
        } finally {
            if (multiLock != null) {
                try {
                    multiLock.unlock();
                    log.info(">>> MultiDistributedLock redis lock end : {}", key);
                } catch (IllegalMonitorStateException e) {
                    log.info("Redisson Lock Already UnLock {} {}", method.getName(), key);
                }
            }
        }
    }
}
