package com.hcommerce.heecommerce.deal.dto;

import com.hcommerce.heecommerce.deal.enums.DiscountType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TimeDealProductDetail {

    private final UUID dealProductUuid;
    private final String dealProductTile;
    private final String productMainImgUrl;
    private final String[] productDetailImgUrls;
    private final int productOriginPrice;
    private final DiscountType dealProductDiscountType;
    private final int dealProductDiscountValue;
    private final int dealProductDealQuantity;
    private final int maxOrderQuantityPerOrder;
    private final Instant startedAt;
    private final Instant finishedAt;

    @Builder
    public TimeDealProductDetail(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgUrl,
        String[] productDetailImgUrls,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity,
        int maxOrderQuantityPerOrder,
        Instant startedAt,
        Instant finishedAt
    ) {
        this.dealProductUuid = dealProductUuid;
        this.dealProductTile = dealProductTile;
        this.productMainImgUrl = productMainImgUrl;
        this.productDetailImgUrls = productDetailImgUrls;
        this.productOriginPrice = productOriginPrice;
        this.dealProductDiscountType = dealProductDiscountType;
        this.dealProductDiscountValue = dealProductDiscountValue;
        this.dealProductDealQuantity = dealProductDealQuantity;
        this.maxOrderQuantityPerOrder = maxOrderQuantityPerOrder;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
}
