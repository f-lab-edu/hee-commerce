package com.hcommerce.heecommerce.common.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public List<T> getAllByKey(String key, TypeReference<T> objectTypeReference) {
        List<T> list = new ArrayList<>();

        Map<String, String> map = hashOperations.entries(key);

        for (String hashKey : map.keySet()) {
            String hashValue = map.get(hashKey);
            list.add(convertObjectFromString(hashValue, objectTypeReference));
        }

        return list;
    }

    public boolean hasKey(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
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
        ObjectMapper objectMapper = createObjectMapper();

        try {
            return objectMapper.readValue(item, objectTypeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertStringFromObject(T item) {
        ObjectMapper objectMapper = createObjectMapper();

        try {
            return objectMapper.writeValueAsString(item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * createObjectMapper 함수 는 ObjectMapper를 재정의하면서 만들 수 있는 함수이다.
     *
     * objectMapper.registerModule(new JavaTimeModule()); 이 코드는 Jackson 라이브러리에서 제공하는 JavaTimeModule을 등록하는 코드이다.
     * Jackson은 기본적으로 Java 8 이상의 java.time 패키지의 클래스들을 처리할 수 없다.
     * 따라서, 이러한 클래스들을 올바르게 직렬화하고 역직렬화할 수 있도록 JavaTimeModule을 등록해야 한다.
     * JavaTimeModule 모듈은 Java 8 이상의 새로운 날짜 및 시간 API인 java.time 패키지의 클래스들 (예: Instant, LocalDateTime, ZonedDateTime 등)을 직렬화 및 역직렬화할 수 있게 해주는 모듈이다.
     *
     * objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 이 코드는 DATE를 TimeStamp로  저장하는 설정을 비활성화 시키는 코드이다.
     * 이 코드를 추가한 이유는 Redis에 Instant 타입의 데이터를 2023-07-08T02:00:00Z 포멧으로 저장하기 위해서이다.
     * 이 코드를 추가하지 않으면, Redis에 Instant 날짜가 1688778000.000000000 포멧으로 저장된다.
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule()); // Java 8 이상 날짜 클래스들(Instant 등)을 직렬화/역직렬화하기 위해 필요

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // DATE를 TimeStamp로  저장하지 않기로 설정

        return objectMapper;
    }
}
