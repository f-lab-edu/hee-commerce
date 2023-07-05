package com.hcommerce.heecommerce.common.dao;

import java.util.List;
import java.util.Set;
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

    /**
     * getMany 는 요청된 key의 순서대로 Redis에 Strings 데이터 타입으로 저장된 여러 데이터들을 얻는 함수이다.
     * 만약, 존재하지 않는 key 가 있으면, 그 key 순서의 결과값은 null 이다.
     * <예시1>
     * redis> MGET key1 key2 nonExistingKey
     * 1) "Hello"
     * 2) "World"
     * 3) (nil)
     * <예시2>
     * redis> MGET key1 nonExistingKey key2
     * 1) "Hello"
     * 2) (nil)
     * 3) "World"
     *
     * Set 으로 한 이유는 중복된 key 검색을 방지를 하기 위해서이다.
     */
    public List<String> getMany(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public long decreaseByAmount(String key, long amount) {
        return redisTemplate.opsForValue().decrement(key, amount); // 감소시킨 후의 결과값이 return된다.
    }

    public long increaseByAmount(String key, long amount) {
        return redisTemplate.opsForValue().increment(key, amount); // 증가시킨 후의 결과값이 return된다.
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
