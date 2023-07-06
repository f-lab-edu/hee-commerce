package com.hcommerce.heecommerce.deal;

import java.util.UUID;

public class DealProductRepository {

    private final static String DEAL_PRODUCT_REDIS_KEY_PREFIX = "timeDealProducts:";

    protected String getRedisKeyForDealProductUuids(String dealOpenDate, int pageNumber) {
        return DealType.TIME_DEAL+":"+dealOpenDate+":"+pageNumber;
    }

    protected String getRedisKey(String dealOpenDate) {
        return DEAL_PRODUCT_REDIS_KEY_PREFIX + dealOpenDate;
    }

    protected String getRedisHashKey(UUID dealProductUuid) {
        return dealProductUuid.toString();
    }
}
