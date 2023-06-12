package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryQueryRepository {

    private final RedisStringsRepository redisStringsRepository;

    @Autowired
    public InventoryQueryRepository(RedisStringsRepository redisStringsRepository) {
        this.redisStringsRepository = redisStringsRepository;
    }

    public int get(String key) {
        return Integer.valueOf(redisStringsRepository.get(key));
    }
}
