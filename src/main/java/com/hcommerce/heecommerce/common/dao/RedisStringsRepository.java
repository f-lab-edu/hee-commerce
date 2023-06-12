package com.hcommerce.heecommerce.common.dao;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisStringsRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisStringsRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * set는 Redis에 Strings 데이터 타입으로 데이터를 저장하는 함수이다.
     * 만약, 동일한 key 가 이미 있으면, 새로운 데이터로 overwrite 된다.
     * 만약, redisTemplate.opsForValue().set("myKey", "myValue", 1000, TimeUnit.SECONDS) 명령어를 실행하고,
     * redisTemplate.opsForValue().set("myKey", "myValue2") 명령어를 실행한 경우,
     * 1번째 명령어에서 설정한 expire time 값은 무효화 되면서 만료시간이 없어지게 된다.
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * setWithExpirationTime는 Redis에 Strings 데이터 타입으로 데이터를 저장하면서 expire time을 설정하는 함수이다.
     * 만약, 동일한 key 가 이미 있으면, 새로운 데이터 및 새로운 expire time으로 overwrite 된다.
     */
    public void setWithExpirationTime(String key, String value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public long decreaseByAmount(String key, long amount) {
        return redisTemplate.opsForValue().decrement(key, amount); // 감소시킨 후의 결과값이 return된다.
    }

    public long increaseByAmount(String key, long amount) {
        return redisTemplate.opsForValue().increment(key, amount); // 증가시킨 후의 결과값이 return된다.
    }
}
