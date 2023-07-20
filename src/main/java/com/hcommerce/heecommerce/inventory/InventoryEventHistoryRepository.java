package com.hcommerce.heecommerce.inventory;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.inventory.dto.InventoryEventHistorySaveDto;
import com.hcommerce.heecommerce.inventory.entity.InventoryEventHistoryEntity;
import com.hcommerce.heecommerce.inventory.mapper.InventoryEventHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryEventHistoryRepository {

    private final InventoryEventHistoryMapper inventoryEventHistoryMapper;

    @Autowired
    public InventoryEventHistoryRepository(InventoryEventHistoryMapper inventoryEventHistoryMapper) {
        this.inventoryEventHistoryMapper = inventoryEventHistoryMapper;
    }

    public void save(InventoryEventHistorySaveDto inventoryEventHistorySaveDto) {
        inventoryEventHistoryMapper.save(InventoryEventHistoryEntity.builder()
            .dealProductUuid(TypeConversionUtils.convertUuidToBinary(inventoryEventHistorySaveDto.getDealProductUuid()))
            .orderUuid(TypeConversionUtils.convertUuidToBinary(inventoryEventHistorySaveDto.getOrderUuid()))
            .inventory(inventoryEventHistorySaveDto.getInventory())
            .previousDealQuantity(inventoryEventHistorySaveDto.getPreviousDealQuantity())
            .inventoryEventType(inventoryEventHistorySaveDto.getInventoryEventType())
            .build()
        );
    }
}
