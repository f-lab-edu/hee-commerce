package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.deal.dto.DealProductSummary;
import com.hcommerce.heecommerce.deal.enums.DiscountType;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
class DealProductSummaryTest {

    private List<DealProductSummary> dealProductsFixture;

    private Instant STARTED_AT = Instant.now();

    private Instant FINISHED_AT = Instant.now().plus(1, ChronoUnit.HOURS);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dealProductsFixture = new ArrayList<>();

        dealProductsFixture.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        dealProductsFixture.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        dealProductsFixture.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
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
        List<DealProductSummary> expectedDealProducts = new ArrayList<>();

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        // when
        List<DealProductSummary> resultDealProducts = DealProductSummary.sortDealProducts(dealProductsFixture, ProductsSort.PRICE_ASC);

        // given
        for (int i = 0; i < expectedDealProducts.size(); i++) {
            Assertions.assertEquals(expectedDealProducts.get(i).getProductOriginPrice(), resultDealProducts.get(i).getProductOriginPrice());
        }
    }

    @DisplayName("sorted in descending order of price")
    @Test
    void It_Sorted_In_Descending_Order_Of_Price() {
        // given
        List<DealProductSummary> expectedDealProducts = new ArrayList<>();

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342073"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342075"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(2000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        expectedDealProducts.add(DealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(1000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .startedAt(STARTED_AT)
            .finishedAt(FINISHED_AT)
            .build());

        // when
        List<DealProductSummary> resultDealProducts = DealProductSummary.sortDealProducts(dealProductsFixture, ProductsSort.PRICE_DESC);

        // given
        for (int i = 0; i < expectedDealProducts.size(); i++) {
            Assertions.assertEquals(expectedDealProducts.get(i).getProductOriginPrice(), resultDealProducts.get(i).getProductOriginPrice());
        }
    }
}
