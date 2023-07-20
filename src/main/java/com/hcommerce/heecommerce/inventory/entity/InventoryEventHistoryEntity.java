package com.hcommerce.heecommerce.inventory.entity;

import com.hcommerce.heecommerce.inventory.enums.InventoryEventType;
import lombok.Builder;
import lombok.Getter;

/**
 * InventoryEventHistoryEntity 는 재고 이벤트 히스토리를 저장할 때 필요한 데이터를 담는 클래스이다.
 */
@Getter
public class InventoryEventHistoryEntity {

    private final byte[] dealProductUuid;
    private final byte[] orderUuid;
    private final int inventory;
    private final int previousDealQuantity;
    private final InventoryEventType inventoryEventType;

    @Builder
    public InventoryEventHistoryEntity(
        byte[] dealProductUuid,
        byte[] orderUuid,
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
