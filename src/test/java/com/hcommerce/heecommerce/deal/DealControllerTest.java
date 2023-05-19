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

    private DealProduct dealProductFixture;

    private PageDto pageDtoFixture;

    private ResponseDto  responseDtoFixture;

    private static final int PAGE_NUMBER = 0;

    private static final int TOTAL_COUNT = 999;
    private static final ProductsSort SORT = ProductsSort.BASIC;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dealProductFixture = DealProduct.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .dealProductTitle("1000원 할인 상품 1")
                .mainImgThumbnailUrl("/test.png")
                .originPrice(3000)
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(1000)
                .dealQuantity(3)
                .maxDealOrderQuantityPerOrder(10)
                .build();

        List<DealProduct> dealProducts = new ArrayList<>();
        dealProducts.add(dealProductFixture);

        pageDtoFixture = PageDto.<DealProduct>builder()
                .pageNumber(PAGE_NUMBER)
                .pageSize(20)
                .totalCount(1)
                .items(dealProducts)
                .build();
    }

    @Nested
    @DisplayName("GET /dealProducts?pageNumber={pageNumber}&sort={sort}")
    class Describe_GET_Deal_Products_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/dealProducts")
                            .queryParam("pageNumber", String.valueOf(PAGE_NUMBER))
                            .queryParam("sort", String.valueOf(SORT))
            );

            // then
            responseDtoFixture = ResponseDto.builder()
                    .code(HttpStatus.OK.name())
                    .message("성공")
                    .data(pageDtoFixture)
                    .build();

            resultActions.andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(responseDtoFixture)))
                    .andDo(DealControllerRestDocs.getDealProducts());
        }
    }
}
