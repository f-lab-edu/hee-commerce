package com.hcommerce.heecommerce.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DealService")
@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @InjectMocks
    private DealService dealService;

    private static final DealType DEAL_TYPE_TIME_DEAL = DealType.TIME_DEAL;

    private static final int PAGE_NUMBER = 0;

    private static final ProductsSort SORT = ProductsSort.BASIC;

    private List<TimeDealProductSummary> dealProductsFixture = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dealProductsFixture.add(TimeDealProductSummary.builder()
            .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
            .dealProductTile("1000원 할인 상품 1")
            .productMainImgThumbnailUrl("/test.png")
            .productOriginPrice(3000)
            .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
            .dealProductDiscountValue(1000)
            .dealProductDealQuantity(3)
            .dealProductStatus(DealProductStatus.BEFORE_OPEN)
            .build()
        );
    }

    @Nested
    @DisplayName("getDealProductsByDealId")
    class Describe_GetDealProductsByDealId {
        @Test
        @DisplayName("return dealProducts")
        void It_return_dealProducts() {
            // given
//            given(dealQueryRepository.findDealProductsByDealId(DEAL_TYPE_TIME_DEAL, PAGE_NUMBER, SORT)).willReturn(dealProductsFixture);

            // when
            List<TimeDealProductSummary> dealProducts = dealService.getDealProductsByDealType(DEAL_TYPE_TIME_DEAL, PAGE_NUMBER, SORT);

            // then
            assertEquals(dealProducts.size(), dealProductsFixture.size());
        }
    }
}
