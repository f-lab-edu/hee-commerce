package com.hcommerce.heecommerce.deal;


import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DealProductsItem {

    private final UUID dealProductUuid;
    private final String dealProductTile;
    private final String productMainImgThumbnailUrl;
    private final int productOriginPrice;
    private final DiscountType dealProductDiscountType;
    private final int dealProductDiscountValue;
    private final int dealProductDealQuantity;

    @Builder
    public DealProductsItem(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgThumbnailUrl,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity
    ) {
        this.dealProductUuid = dealProductUuid;
        this.dealProductTile = dealProductTile;
        this.productMainImgThumbnailUrl = productMainImgThumbnailUrl;
        this.productOriginPrice = productOriginPrice;
        this.dealProductDiscountType = dealProductDiscountType;
        this.dealProductDiscountValue = dealProductDiscountValue;
        this.dealProductDealQuantity = dealProductDealQuantity;
    }
}
