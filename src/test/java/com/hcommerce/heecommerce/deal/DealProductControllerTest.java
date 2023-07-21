package com.hcommerce.heecommerce.deal;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@DisplayName("DealController")
class DealProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealProductService dealProductService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DealProductSummary dealProductSummaryFixture;

    private List<DealProductSummary> dealProductsFixture;

    private PageDto pageDtoFixture;

    private ResponseDto  responseDtoFixture;

    private static final DealType DEAL_TYPE_TIME_DEAL = DealType.TIME_DEAL;

    private static final UUID DEAL_PRODUCT_UUID = UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077");

    private static final int PAGE_NUMBER = 0;

    private static final int TOTAL_COUNT = 999;

    private static final ProductsSort SORT = ProductsSort.BASIC;

    private Instant STARTED_AT = Instant.now();

    private Instant FINISHED_AT = Instant.now().plus(1, ChronoUnit.HOURS);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        dealProductSummaryFixture = DealProductSummary.builder()
                .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .dealProductTile("1000원 할인 상품 1")
                .productMainImgThumbnailUrl("/test.png")
                .productOriginPrice(3000)
                .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                .dealProductDiscountValue(1000)
                .dealProductDealQuantity(3)
                .startedAt(STARTED_AT)
                .finishedAt(FINISHED_AT)
                .build();

        dealProductsFixture = new ArrayList<>();

        dealProductsFixture.add(dealProductSummaryFixture);

        pageDtoFixture = PageDto.<DealProductSummary>builder()
                .pageNumber(PAGE_NUMBER)
                .pageSize(20)
                .totalCount(dealProductsFixture.size())
                .items(dealProductsFixture)
                .build();
    }

    @Nested
    @DisplayName("GET /deals/{dealType}/dealProducts?pageNumber={pageNumber}&sort={sort}")
    class Describe_GET_Deal_Products_By_Deal_Type_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // given
            given(
                dealProductService.getDealProductsByDealType(DEAL_TYPE_TIME_DEAL, PAGE_NUMBER, SORT)).willReturn(dealProductsFixture);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/deals/{dealType}/dealProducts", DEAL_TYPE_TIME_DEAL)
                            .queryParam("pageNumber", String.valueOf(PAGE_NUMBER))
                            .queryParam("sort", String.valueOf(SORT))
            );

            // then
            responseDtoFixture = ResponseDto.builder()
                    .code(HttpStatus.OK.name())
                    .message("딜 상품 목록 조회 성공하였습니다.")
                    .data(pageDtoFixture)
                    .build();

            resultActions.andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(responseDtoFixture)))
                    .andDo(DealControllerRestDocs.getDealProductsByDealType());
        }
    }

    @Nested
    @DisplayName("GET /dealProducts/{dealProductUuid}")
    class Describe_GET_Deal_Product_Detail_By_Deal_Product_Uuid_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // given
            TimeDealProductDetail timeDealProductDetailFixture = TimeDealProductDetail.builder()
                .dealProductUuid(DEAL_PRODUCT_UUID)
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

            given(dealProductService.getTimeDealProductDetailByDealProductUuid(DEAL_PRODUCT_UUID)).willReturn(timeDealProductDetailFixture);

            // when
            ResultActions resultActions = mockMvc.perform(
                get("/dealProducts/{dealProductUuid}", DEAL_PRODUCT_UUID)
            );

            // then
            responseDtoFixture = ResponseDto.builder()
                .code(HttpStatus.OK.name())
                .message("딜 상품 상세보기 조회 성공하였습니다.")
                .data(timeDealProductDetailFixture)
                .build();

            resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDtoFixture)))
                .andDo(DealControllerRestDocs.getDealProductDetailByDealProductUuid());
        }
    }
}
