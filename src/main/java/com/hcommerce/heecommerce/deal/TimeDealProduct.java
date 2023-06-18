package com.hcommerce.heecommerce.deal;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TimeDealProduct {
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

    @Builder
    public TimeDealProduct(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgUrl,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity,
        String[] productDetailImgUrls,
        String productMainImgThumbnailUrl,
        DealProductStatus dealProductStatus
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
    }
}
