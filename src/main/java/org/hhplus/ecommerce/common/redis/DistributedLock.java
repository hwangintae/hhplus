package org.hhplus.ecommerce.common.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key();
    String prefix();
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    long waitTime() default 5L;
    long leaseTime() default 10L;
}
