package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import java.util.List;
import java.util.Set;
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

    public List<Integer> getMany(Set<String> keys) {
        List<String> inventories = redisStringsRepository.getMany(keys);

        return TypeConversionUtils.convertStringsToIntegers(inventories);
    }
}
