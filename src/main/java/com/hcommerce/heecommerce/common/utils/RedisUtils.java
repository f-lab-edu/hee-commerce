package com.hcommerce.heecommerce.common.utils;

import java.util.UUID;

public class RedisUtils {

    private final static String INVENTORY_REDIS_KEY_PREFIX = "timeDealProductInventory:";

    public static String getInventoryKey(UUID dealProductUuid) {
        return INVENTORY_REDIS_KEY_PREFIX + dealProductUuid;
    }
}
