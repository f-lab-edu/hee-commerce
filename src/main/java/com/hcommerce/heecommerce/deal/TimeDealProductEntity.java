package com.hcommerce.heecommerce.deal;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;


/**
 * TimeDealProductEntity는 Redis에 저장되는 딜 상품 클래스이다.
 *
 * 딜 상품은 Redis에 다음과 같은 형태로 저장된다.
 * RedisKey : timeDealProducts:{dealOpenDate(yyyyMMdd)}
 * HashKey : {dealProductUuid}
 * HashValue : {TimeDealProductEntity}
 *
 */
@Getter
public class TimeDealProductEntity {

    private final UUID dealProductUuid;
    private final String dealProductTile;
    private final String productMainImgUrl;
    private final int productOriginPrice;
    private final DiscountType dealProductDiscountType;
    private final int dealProductDiscountValue;
    private final int dealProductDealQuantity;
    private final String[] productDetailImgUrls;
    private final String productMainImgThumbnailUrl;
    private final int maxOrderQuantityPerOrder;
    private final Instant startedAt;
    private final Instant finishedAt;

    @Builder
    @ConstructorProperties({
        "dealProductUuid",
        "dealProductTile",
        "productMainImgUrl",
        "productOriginPrice",
        "dealProductDiscountType",
        "dealProductDiscountValue",
        "dealProductDealQuantity",
        "productDetailImgUrls",
        "productMainImgThumbnailUrl",
        "maxOrderQuantityPerOrder",
        "startedAt",
        "finishedAt"
    })
    public TimeDealProductEntity(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgUrl,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity,
        String[] productDetailImgUrls,
        String productMainImgThumbnailUrl,
        int maxOrderQuantityPerOrder,
        Instant startedAt,
        Instant finishedAt
    ) {
        this.dealProductUuid = dealProductUuid;
        this.dealProductTile = dealProductTile;
        this.productMainImgUrl = productMainImgUrl;
        this.productOriginPrice = productOriginPrice;
        this.dealProductDiscountType = dealProductDiscountType;
        this.dealProductDiscountValue = dealProductDiscountValue;
        this.dealProductDealQuantity = dealProductDealQuantity;
        this.productDetailImgUrls = productDetailImgUrls;
        this.productMainImgThumbnailUrl = productMainImgThumbnailUrl;
        this.maxOrderQuantityPerOrder = maxOrderQuantityPerOrder;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
}
