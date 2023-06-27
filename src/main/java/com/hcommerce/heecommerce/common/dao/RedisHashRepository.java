package com.hcommerce.heecommerce.common.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisHashRepository<T> {

    private final RedisTemplate<String, String> redisTemplate;

    private final HashOperations<String, String, String> hashOperations;

    public RedisHashRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public T getOneByKeyAndHashKey(String key, String hashKey, TypeReference<T> objectTypeReference) {
        String item = (String) hashOperations.get(key, hashKey);

        return convertObjectFromString(item, objectTypeReference);
    }

    public boolean hasKey(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    public List<T> getAllByKey(String key, TypeReference<T> objectTypeReference) {
        List<T> list = new ArrayList<>();

        Map<String, String> map = hashOperations.entries(key);

        for (String hashKey : map.keySet()) {
            String hashValue = map.get(hashKey);
            list.add(convertObjectFromString(hashValue, objectTypeReference));
        }

        return list;
    }

    public void put(String key, String hashKey, T item) {
        String itemString = convertStringFromObject(item);

        hashOperations.put(key, hashKey, itemString);
    }

    public void putWithExpirationTime(String key, String hashKey, T item, long time, TimeUnit timeUnit) {
        String itemString = convertStringFromObject(item);

        hashOperations.put(key, hashKey, itemString);

        redisTemplate.expire(key, time, timeUnit);
    }

    private T convertObjectFromString(String item, TypeReference<T> objectTypeReference) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(item, objectTypeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertStringFromObject(T item) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
