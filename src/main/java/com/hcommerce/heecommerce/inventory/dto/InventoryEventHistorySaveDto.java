package com.hcommerce.heecommerce.inventory.dto;

import com.hcommerce.heecommerce.inventory.enums.InventoryEventType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InventoryEventHistorySaveDto {

    private final UUID dealProductUuid;
    private final UUID orderUuid;
    private final int inventory;
    private final int previousDealQuantity;
    private final InventoryEventType inventoryEventType;

    @Builder
    public InventoryEventHistorySaveDto(
        UUID dealProductUuid,
        UUID orderUuid,
        int inventory,
        int previousDealQuantity,
        InventoryEventType inventoryEventType
    ) {
        this.dealProductUuid = dealProductUuid;
        this.orderUuid = orderUuid;
        this.inventory = inventory;
        this.previousDealQuantity = previousDealQuantity;
        this.inventoryEventType = inventoryEventType;
    }
}
