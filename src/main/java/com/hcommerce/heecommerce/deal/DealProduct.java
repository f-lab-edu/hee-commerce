package com.hcommerce.heecommerce.deal;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DealProduct {
    private final UUID productUuid;
    private final String dealProductTitle;
    private final String mainImgThumbnailUrl;
    private final int originPrice;
    private final DiscountType discountType;
    private final int discountValue;
    private final int dealQuantity;
    private final int maxDealOrderQuantityPerOrder;

    @Builder
    public DealProduct(
        UUID productUuid,
        String dealProductTitle,
        String mainImgThumbnailUrl,
        int originPrice,
        DiscountType discountType,
        int discountValue,
        int dealQuantity,
        int maxDealOrderQuantityPerOrder
    ) {
        this.productUuid = productUuid;
        this.dealProductTitle = dealProductTitle;
        this.mainImgThumbnailUrl = mainImgThumbnailUrl;
        this.originPrice = originPrice;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.dealQuantity = dealQuantity;
        this.maxDealOrderQuantityPerOrder = maxDealOrderQuantityPerOrder;
    }
}
