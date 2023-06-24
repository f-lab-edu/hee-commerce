package com.hcommerce.heecommerce.deal;

import java.beans.ConstructorProperties;
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
    private final int maxOrderQuantityPerOrder;

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
        "maxOrderQuantityPerOrder"
    })
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
        DealProductStatus dealProductStatus,
        int maxOrderQuantityPerOrder
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
    }
}
