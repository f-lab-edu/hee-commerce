package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DealService {

    public List<TimeDealProductSummary> getDealProductsByDealType(DealType dealType, int pageNumber, ProductsSort sort) {

        List<TimeDealProductSummary> dealProducts = new ArrayList<>();
        dealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        return dealProducts;
    }
}
