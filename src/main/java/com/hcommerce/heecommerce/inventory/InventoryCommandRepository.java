package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import com.hcommerce.heecommerce.common.utils.RedisUtils;
import java.util.UUID;
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

    public void set(UUID dealProductUuid, int amount) {
        String key = RedisUtils.getInventoryKey(dealProductUuid);

        redisStringsRepository.set(key, String.valueOf(amount));
    }

    public void delete(UUID dealProductUuid) {
        String key = RedisUtils.getInventoryKey(dealProductUuid);

        redisStringsRepository.delete(key);
    }

    public int decreaseByAmount(UUID dealProductUuid, int amount) {
        validationAmountIsPositive(amount);

        String key = RedisUtils.getInventoryKey(dealProductUuid);

        return (int) redisStringsRepository.decreaseByAmount(key, Long.valueOf(amount));
    }

    public void increaseByAmount(UUID dealProductUuid, int amount) {
        validationAmountIsPositive(amount);

        String key = RedisUtils.getInventoryKey(dealProductUuid);

        redisStringsRepository.increaseByAmount(key, Long.valueOf(amount));
    }

    private void validationAmountIsPositive(int amount) {
        if(amount <= 0) {
            throw new AmountIsNotPositiveException();
        }
    }
}
