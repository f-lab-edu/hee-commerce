package com.hcommerce.heecommerce.common;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisLockHelper {

    private final RedissonClient redissonClient;

    @Autowired
    public RedisLockHelper(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    public boolean tryLock(RLock lock, long waitTime, long leaseTime) throws InterruptedException {
        return lock.tryLock(waitTime, leaseTime, TimeUnit.MICROSECONDS);
    }

    public void unlock(RLock lock) {
        lock.unlock();
    }
}
