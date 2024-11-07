package org.hhplus.ecommerce.integrationTest;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer extends GenericContainer<RedisContainer> {
    private static final int REDIS_PORT = 6379;

    public RedisContainer(String redisImageName) {
        super(redisImageName);
        this.withExposedPorts(REDIS_PORT);
        this.withReuse(true);
    }

    public Integer getRedisPort() {
        return this.getMappedPort(REDIS_PORT);
    }
}
