package com.hcommerce.heecommerce.inventory;

import java.util.UUID;

public class InventoryRepository {

    private final static String INVENTORY_REDIS_KEY_PREFIX = "timeDealProductInventory:";

    protected String getRedisKey(UUID dealProductUuid) {
        return INVENTORY_REDIS_KEY_PREFIX + dealProductUuid;
    }
}
