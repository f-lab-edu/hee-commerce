package com.hcommerce.heecommerce.fixture;

import com.hcommerce.heecommerce.deal.enums.DiscountType;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class DealProductFixture {

    public static final UUID DEAL_PRODUCT_UUID = UUID.randomUUID();

    public static final int PRODUCT_ORIGIN_PRICE = 3000;

    public static final DiscountType DEAL_PRODUCT_DISCOUNT_TYPE = DiscountType.FIXED_AMOUNT;

    public static final int DEAL_PRODUCT_DISCOUNT_VALUE = 1000;

    public static final int DEAL_PRODUCT_DEAL_QUANTITY = 3;

    public static final int MAX_ORDER_QUANTITY_PER_ORDER = 10;

    public static final Instant STARTED_AT = Instant.now();

    public static final Instant FINISHED_AT = Instant.now().plus(1, ChronoUnit.HOURS);

    public static final TimeDealProductDetail timeDealProductDetail = TimeDealProductDetail.builder()
        .dealProductUuid(DEAL_PRODUCT_UUID)
        .dealProductTile("1000원 할인 상품 1")
        .productMainImgUrl("/test.png")
        .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
        .productOriginPrice(PRODUCT_ORIGIN_PRICE)
        .dealProductDiscountType(DEAL_PRODUCT_DISCOUNT_TYPE)
        .dealProductDiscountValue(DEAL_PRODUCT_DISCOUNT_VALUE)
        .dealProductDealQuantity(DEAL_PRODUCT_DEAL_QUANTITY)
        .maxOrderQuantityPerOrder(MAX_ORDER_QUANTITY_PER_ORDER)
        .startedAt(STARTED_AT)
        .finishedAt(FINISHED_AT)
        .build();
}
