package com.hcommerce.heecommerce.inventory;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InventoryHistoryItem {

    private final UUID dealProductUuid;
    private final int userId;
    private final UUID orderUuid;
    private final int orderQuantity;
    private final int previousDealQuantity;

    @Builder
    public InventoryHistoryItem(
        UUID dealProductUuid,
        int userId,
        UUID orderUuid,
        int orderQuantity,
        int previousDealQuantity
    ) {
        this.dealProductUuid = dealProductUuid;
        this.userId = userId;
        this.orderUuid = orderUuid;
        this.orderQuantity = orderQuantity;
        this.previousDealQuantity = previousDealQuantity;
    }
}
