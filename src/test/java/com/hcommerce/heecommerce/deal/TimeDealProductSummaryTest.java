package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

@DisplayName("TimeDealProductSummary")
class TimeDealProductSummaryTest {

    private List<TimeDealProductSummary> dealProductsFixture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dealProductsFixture = new ArrayList<>();

        dealProductsFixture.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        dealProductsFixture.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        dealProductsFixture.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());
    }

    @AfterEach
    void tearDown() {
        dealProductsFixture = new ArrayList<>();
    }

    @Test
    @DisplayName("sorted in ascending order of price")
    void It_Sorted_In_Ascending_Order_Of_Price() {
        // given
        List<TimeDealProductSummary> expectedDealProducts = new ArrayList<>();

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        // when
        List<TimeDealProductSummary> resultDealProducts = TimeDealProductSummary.sortDealProducts(dealProductsFixture, ProductsSort.PRICE_ASC);

        // given
        for (int i = 0; i < expectedDealProducts.size(); i++) {
            Assertions.assertEquals(expectedDealProducts.get(i).getProductOriginPrice(), resultDealProducts.get(i).getProductOriginPrice());
        }
    }

    @DisplayName("sorted in descending order of price")
    @Test
    void It_Sorted_In_Descending_Order_Of_Price() {
        // given
        List<TimeDealProductSummary> expectedDealProducts = new ArrayList<>();

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        expectedDealProducts.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build());

        // when
        List<TimeDealProductSummary> resultDealProducts = TimeDealProductSummary.sortDealProducts(dealProductsFixture, ProductsSort.PRICE_DESC);

        // given
        for (int i = 0; i < expectedDealProducts.size(); i++) {
            Assertions.assertEquals(expectedDealProducts.get(i).getProductOriginPrice(), resultDealProducts.get(i).getProductOriginPrice());
        }
    }
}
