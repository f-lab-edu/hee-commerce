package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryCommandRepository {

    private final RedisStringsRepository redisStringsRepository;

    @Autowired
    public InventoryCommandRepository(
        RedisStringsRepository redisStringsRepository
    ) {
        this.redisStringsRepository = redisStringsRepository;
    }

    public void set(String key, int amount) {
        redisStringsRepository.set(key, String.valueOf(amount));
    }

    public void delete(String key) {
        redisStringsRepository.delete(key);
    }

    public int decreaseByAmount(String key, int amount) {
        return (int) redisStringsRepository.decreaseByAmount(key, Long.valueOf(amount));
    }

    public void increaseByAmount(String key, int amount) {
        redisStringsRepository.increaseByAmount(key, Long.valueOf(amount));
    }
}
