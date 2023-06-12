package com.hcommerce.heecommerce.common.dao;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisSortSetRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;

    private final ZSetOperations<String, T> zSetOps;

    public RedisSortSetRepository(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOps = redisTemplate.opsForZSet();
    }

    public Set<T> getAll(String key) {
        return zSetOps.range(key, 0, -1);
    }

    public void add(String key, T item, int score) {
        zSetOps.add(key, item, score);
    }

    public void addWithExpirationTime(String key, T item, int score, long time, TimeUnit timeUnit) {
        zSetOps.add(key, item, score);

        redisTemplate.expire(key, time, timeUnit);
    }
}
