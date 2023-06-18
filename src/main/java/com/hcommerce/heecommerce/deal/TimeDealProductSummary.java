package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TimeDealProductSummary {

    private final UUID dealProductUuid;
    private final String dealProductTile;
    private final String productMainImgThumbnailUrl;
    private final int productOriginPrice;
    private final DiscountType dealProductDiscountType;
    private final int dealProductDiscountValue;
    private final int dealProductDealQuantity;
    private final DealProductStatus dealProductStatus;

    @Builder
    public TimeDealProductSummary(
        UUID dealProductUuid,
        String dealProductTile,
        String productMainImgThumbnailUrl,
        int productOriginPrice,
        DiscountType dealProductDiscountType,
        int dealProductDiscountValue,
        int dealProductDealQuantity,
        DealProductStatus dealProductStatus
    ) {
        this.dealProductUuid = dealProductUuid;
        this.dealProductTile = dealProductTile;
        this.productMainImgThumbnailUrl = productMainImgThumbnailUrl;
        this.productOriginPrice = productOriginPrice;
        this.dealProductDiscountType = dealProductDiscountType;
        this.dealProductDiscountValue = dealProductDiscountValue;
        this.dealProductDealQuantity = dealProductDealQuantity;
        this.dealProductStatus = dealProductStatus;
    }

    public static List<TimeDealProductSummary> sortDealProducts(List<TimeDealProductSummary> dealProducts, ProductsSort sort) {
        List<TimeDealProductSummary> newDealProducts = new ArrayList<>(dealProducts);

        if(sort == ProductsSort.PRICE_ASC) {
            Collections.sort(newDealProducts, originPriceAscComparator);

            return newDealProducts;
        } else if (sort == ProductsSort.PRICE_DESC) {
            Collections.sort(newDealProducts, originPriceAscComparator.reversed());

            return newDealProducts;
        } else {
            return dealProducts;
        }
    }

    private static Comparator<TimeDealProductSummary> originPriceAscComparator = new Comparator<TimeDealProductSummary>() {
        @Override
        public int compare(TimeDealProductSummary o1, TimeDealProductSummary o2) {
            return o1.getProductOriginPrice() - o2.getProductOriginPrice();
        }
    };
}
