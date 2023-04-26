package com.hcommerce.heecommerce.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProductsByCenterId() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/{center_id}", 2))
                .andExpect(status().isOk())
                .andDo(ProductDocumentation.getProductsByCenterId());
    }

    class ProductDocumentation {
        private static RestDocumentationResultHandler getProductsByCenterId() {
            return document("get-products-by-center-id",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                            parameterWithName("center_id").description("센터 ID")
                    ),
                    responseFields(
                            fieldWithPath("products[].id").description("상품 ID17"),
                            fieldWithPath("products[].name").description("상품 이름"),
                            fieldWithPath("products[].quantity").description("상품 재고 수량")
//                            fieldWithPath("totalElement").description("페이지 번호"),
//                            fieldWithPath("pageNumber").description("페이지 번호"),
//                            fieldWithPath("pageSize").description("페이지당 개수")
                            )
            );
        }
    }
}