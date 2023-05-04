package com.hcommerce.heecommerce.product;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private GetProductsByCenterIdResponseDTO getProductsByCenterIdResponseDTOFixture;

    private final int CENTER_ID = 0;

    private int DEFAULT_PAGE_NUMBER = 0;

    private int DEFAULT_PAGE_SIZE = 20;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productFixture = Product.builder()
                .productUuid(UUID.fromString("01b8851c-d046-4635-83c1-eb0ca4342077"))
                .name("상품1")
                .mainImgUrl("/test.png")
                .maxOrderQuantityPerOrder(10)
                .price(3)
                .inventoryQuantity(3000)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(productFixture);

        getProductsByCenterIdResponseDTOFixture = GetProductsByCenterIdResponseDTO.builder()
                .pageNumber(0)
                .pageSize(20)
                .totalElement(999)
                .products(products)
                .build();
    }

    @Nested
    @DisplayName("GET /products/{centerId}?")
    class Describe_GET_Products_By_Center_Id_API {
        @Test
        @DisplayName("returns 200 ok")
        void It_returns_200_OK() throws Exception {
            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/products/{centerId}?pageNumber={pageNumber}&pageSize={pageSize}", CENTER_ID, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE)
            );

            // then
            resultActions.andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(getProductsByCenterIdResponseDTOFixture)))
                    .andDo(ProductControllerRestDocs.getProductsByCenterId());
        }
    }
}
