package com.hcommerce.heecommerce.inventory.dto;

import com.hcommerce.heecommerce.inventory.enums.InventoryEventType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InventoryIncreaseDecreaseDto {

    private final UUID dealProductUuid;
    private final UUID orderUuid;
    private final int inventory;
    private final InventoryEventType inventoryEventType;

    @Builder
    public InventoryIncreaseDecreaseDto(
        UUID dealProductUuid,
        UUID orderUuid,
        int inventory,
        InventoryEventType inventoryEventType
    ) {
        this.dealProductUuid = dealProductUuid;
        this.orderUuid = orderUuid;
        this.inventory = inventory;
        this.inventoryEventType = inventoryEventType;
    }
}