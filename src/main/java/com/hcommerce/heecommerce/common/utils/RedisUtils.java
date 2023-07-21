package com.hcommerce.heecommerce.common.utils;

import com.hcommerce.heecommerce.deal.enums.DealType;
import java.util.UUID;

public class RedisUtils {

    private final static String INVENTORY_REDIS_KEY_PREFIX = "timeDealProductInventory:";

    private final static String DEAL_PRODUCT_REDIS_KEY_PREFIX = "timeDealProducts:";

    public static String getInventoryKey(UUID dealProductUuid) {
        return INVENTORY_REDIS_KEY_PREFIX + dealProductUuid;
    }

    public static String getKeyForTimeDealProductUuids(String dealOpenDate, int pageNumber) {
        return DealType.TIME_DEAL+":"+dealOpenDate+":"+pageNumber;
    }

    public static String getKeyForTimeDealProductsByDealOpenDate(String dealOpenDate) {
        return DEAL_PRODUCT_REDIS_KEY_PREFIX + dealOpenDate;
    }

    public static String getRedisHashKeyForTimeDealProduct(UUID dealProductUuid) {
        return dealProductUuid.toString();
    }
}
