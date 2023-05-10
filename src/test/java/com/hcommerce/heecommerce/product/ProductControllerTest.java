package com.hcommerce.heecommerce.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.common.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@DisplayName("ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Product productFixture;

    private PageDto pageDtoFixture;

    private static final int CENTER_ID = 0;

    private static final int PAGE_NUMBER = 0;

    private static final int PAGE_SIZE = 20;

    private static final int TOTAL_COUNT = 999;
    private static final ProductsSort SORT = ProductsSort.BASIC;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productFixture = Product.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .name("상품1")
                .mainImgUrl("/test.png")
                .maxOrderQuantityPerOrder(10)
                .price(3000)
                .inventoryQuantity(3)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(productFixture);

        pageDtoFixture = PageDto.<Product>builder()
                .pageNumber(PAGE_NUMBER)
                .pageSize(PAGE_SIZE)
                .totalCount(TOTAL_COUNT)
                .items(products)
                .build();
    }

    @Nested
    @DisplayName("GET /products/{centerId}?pageNumber={pageNumber}&pageSize={pageSize}")
    class Describe_GET_Products_By_Center_Id_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/products/{centerId}", CENTER_ID)
                            .queryParam("pageNumber", String.valueOf(PAGE_NUMBER))
                            .queryParam("pageSize", String.valueOf(PAGE_SIZE))
                            .queryParam("sort", String.valueOf(SORT))
            );

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(pageDtoFixture)))
                    .andDo(ProductControllerRestDocs.getProductsByCenterId());
        }
    }
}
