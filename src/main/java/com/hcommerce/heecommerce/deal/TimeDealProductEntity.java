package com.hcommerce.heecommerce.deal;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;


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
    private final DealProductStatus dealProductStatus;
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
        "dealProductStatus",
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
        DealProductStatus dealProductStatus,
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
        this.dealProductStatus = dealProductStatus;
        this.maxOrderQuantityPerOrder = maxOrderQuantityPerOrder;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
}
