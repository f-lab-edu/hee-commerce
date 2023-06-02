package com.hcommerce.heecommerce.deal;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.common.dto.PageDto;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import com.hcommerce.heecommerce.product.ProductsSort;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@DisplayName("DealController")
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DealProductsItem dealProductsItemFixture;

    private PageDto pageDtoFixture;

    private ResponseDto  responseDtoFixture;

    private static final int TIME_DEAL_ID = 1;

    private static final int PAGE_NUMBER = 0;

    private static final int TOTAL_COUNT = 999;
    private static final ProductsSort SORT = ProductsSort.BASIC;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dealProductsItemFixture = DealProductsItem.builder()
                .dealProductUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .dealProductTile("1000원 할인 상품 1")
                .productMainImgThumbnailUrl("/test.png")
                .productOriginPrice(3000)
                .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                .dealProductDiscountValue(1000)
                .dealProductDealQuantity(3)
                .build();

        List<DealProductsItem> dealProducts = new ArrayList<>();
        dealProducts.add(dealProductsItemFixture);

        pageDtoFixture = PageDto.<DealProductsItem>builder()
                .pageNumber(PAGE_NUMBER)
                .pageSize(20)
                .totalCount(dealProducts.size())
                .items(dealProducts)
                .build();
    }

    @Nested
    @DisplayName("GET /dealProducts/{dealId}?pageNumber={pageNumber}&sort={sort}")
    class Describe_GET_Deal_Products_By_Deal_Id_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/dealProducts/{dealId}", TIME_DEAL_ID)
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
                    .andDo(DealControllerRestDocs.getDealProductsByDealId());
        }
    }
}
