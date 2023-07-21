package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.dao.RedisStringsRepository;
import com.hcommerce.heecommerce.common.utils.RedisUtils;
import com.hcommerce.heecommerce.inventory.dto.InventoryEventHistorySaveDto;
import com.hcommerce.heecommerce.inventory.dto.InventoryIncreaseDecreaseDto;
import com.hcommerce.heecommerce.inventory.exception.AmountIsNotPositiveException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryCommandRepository {

    private final InventoryEventHistoryRepository inventoryEventHistoryRepository;

    private final RedisStringsRepository redisStringsRepository;

    @Autowired
    public InventoryCommandRepository(
        InventoryEventHistoryRepository inventoryEventHistoryRepository,
        RedisStringsRepository redisStringsRepository
    ) {
        this.inventoryEventHistoryRepository = inventoryEventHistoryRepository;
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

    public int decrease(InventoryIncreaseDecreaseDto inventoryIncreaseDecreaseDto) {
        int inventory = inventoryIncreaseDecreaseDto.getInventory();

        int inventoryAfterDecrease = decreaseByAmount(inventoryIncreaseDecreaseDto.getDealProductUuid(), inventory);

        int inventoryBeforeDecrease = inventoryAfterDecrease - inventory;

        InventoryEventHistorySaveDto inventoryEventHistorySaveDto = InventoryEventHistorySaveDto.builder()
            .dealProductUuid(inventoryIncreaseDecreaseDto.getDealProductUuid())
            .orderUuid(inventoryIncreaseDecreaseDto.getOrderUuid())
            .inventory(-inventoryIncreaseDecreaseDto.getInventory())
            .previousDealQuantity(inventoryBeforeDecrease)
            .inventoryEventType(inventoryIncreaseDecreaseDto.getInventoryEventType())
            .build();

        inventoryEventHistoryRepository.save(inventoryEventHistorySaveDto);

        return inventoryAfterDecrease;
    }

    private int decreaseByAmount(UUID dealProductUuid, int amount) {
        validationAmountIsPositive(amount);

        String key = RedisUtils.getInventoryKey(dealProductUuid);

        return (int) redisStringsRepository.decreaseByAmount(key, Long.valueOf(amount));
    }

    public int increase(InventoryIncreaseDecreaseDto inventoryIncreaseDecreaseDto) {
        int inventory = inventoryIncreaseDecreaseDto.getInventory();

        int inventoryAfterIncrease = increaseByAmount(inventoryIncreaseDecreaseDto.getDealProductUuid(), inventory);

        int inventoryBeforeIncrease = inventoryAfterIncrease - inventory;

        InventoryEventHistorySaveDto inventoryEventHistorySaveDto = InventoryEventHistorySaveDto.builder()
            .dealProductUuid(inventoryIncreaseDecreaseDto.getDealProductUuid())
            .orderUuid(inventoryIncreaseDecreaseDto.getOrderUuid())
            .inventory(+inventoryIncreaseDecreaseDto.getInventory())
            .previousDealQuantity(inventoryBeforeIncrease)
            .inventoryEventType(inventoryIncreaseDecreaseDto.getInventoryEventType())
            .build();

        inventoryEventHistoryRepository.save(inventoryEventHistorySaveDto);

        return inventoryAfterIncrease;
    }

    private int increaseByAmount(UUID dealProductUuid, int amount) {
        validationAmountIsPositive(amount);

        String key = RedisUtils.getInventoryKey(dealProductUuid);

        return (int) redisStringsRepository.increaseByAmount(key, Long.valueOf(amount));
    }

    private void validationAmountIsPositive(int amount) {
        if(amount <= 0) {
            throw new AmountIsNotPositiveException();
        }
    }
}
