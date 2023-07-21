package com.hcommerce.heecommerce.deal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.deal.dto.DealProductSummary;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import com.hcommerce.heecommerce.deal.enums.DealType;
import com.hcommerce.heecommerce.deal.enums.DiscountType;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("DealService")
@ExtendWith(MockitoExtension.class)
class DealProductServiceTest {

    @Mock
    private DealProductQueryRepository dealProductQueryRepository;

    @InjectMocks
    private DealProductService dealProductService;

    private static final DealType DEAL_TYPE_TIME_DEAL = DealType.TIME_DEAL;

    private static final int PAGE_NUMBER = 0;

    private static final ProductsSort SORT = ProductsSort.BASIC;

    private List<DealProductSummary> dealProductsFixture = new ArrayList<>();

    private Instant STARTED_AT = Instant.now();

    private Instant FINISHED_AT = Instant.now().plus(1, ChronoUnit.HOURS);

    @BeforeEach
    void setUp() {
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
            given(dealProductQueryRepository.getDealProductsByDealType(DEAL_TYPE_TIME_DEAL, PAGE_NUMBER, SORT)).willReturn(dealProductsFixture);

            // when
            List<DealProductSummary> dealProducts = dealProductService.getDealProductsByDealType(DEAL_TYPE_TIME_DEAL, PAGE_NUMBER, SORT);

            // then
            assertEquals(dealProducts.size(), dealProductsFixture.size());
        }
    }

    @Nested
    @DisplayName("getTimeDealProductDetailByDealProductUuid")
    class Describe_GetTimeDealProductDetailByDealProductEntityUuid {
        @Test
        @DisplayName("return dealProducts")
        void It_return_dealProducts() {
            // given
            UUID dealProductUuid = UUID.randomUUID();

            TimeDealProductDetail timeDealProductDetailFixture = TimeDealProductDetail.builder()
                .dealProductUuid(dealProductUuid)
                .dealProductTile("1000원 할인 상품 1")
                .productMainImgUrl("/test.png")
                .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
                .productOriginPrice(3000)
                .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                .dealProductDiscountValue(1000)
                .dealProductDealQuantity(3)
                .maxOrderQuantityPerOrder(10)
                .startedAt(STARTED_AT)
                .finishedAt(FINISHED_AT)
                .build();

            given(dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(dealProductUuid)).willReturn(timeDealProductDetailFixture);

            // when
            TimeDealProductDetail timeDealProductDetail = dealProductService.getTimeDealProductDetailByDealProductUuid(dealProductUuid);

            // then
            assertEquals(timeDealProductDetail, timeDealProductDetailFixture);
        }
    }
}
