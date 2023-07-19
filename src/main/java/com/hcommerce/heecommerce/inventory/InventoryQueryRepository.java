package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import com.hcommerce.heecommerce.common.utils.RedisUtils;
import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryQueryRepository {

    private final RedisStringsRepository redisStringsRepository;

    @Autowired
    public InventoryQueryRepository(RedisStringsRepository redisStringsRepository) {
        this.redisStringsRepository = redisStringsRepository;
    }

    public int get(UUID dealProductUuid) {
        String key = RedisUtils.getInventoryKey(dealProductUuid);

        return Integer.valueOf(redisStringsRepository.get(key));
    }

    public List<Integer> getMany(Set<String> uuidStrings) {
        Set<String> keys = createRedisKeys(uuidStrings);

        List<String> inventories = redisStringsRepository.getMany(keys);

        return TypeConversionUtils.convertStringsToIntegers(inventories);
    }

    private Set<String> createRedisKeys(Set<String> uuidStrings) {
        Set<String> redisKeys = new HashSet<>();

        Iterator<String> iterator = uuidStrings.iterator();

        while (iterator.hasNext()) {
            String uuid = iterator.next();

            redisKeys.add(RedisUtils.getInventoryKey(UUID.fromString(uuid)));
        }

        return redisKeys;
    }
}
