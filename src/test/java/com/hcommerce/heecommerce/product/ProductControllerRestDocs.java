package com.hcommerce.heecommerce.product;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
public class ProductControllerRestDocs {

    public static RestDocumentationResultHandler getProductsByCenterId() {
        return document("get-products-by-center-id",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("centerId").description("센터 ID")
                ),
                queryParameters(
                        parameterWithName("pageNumber").optional().description("페이지 번호"),
                        parameterWithName("pageSize").optional().description("페이지당 개수"),
                        parameterWithName("sort").optional().description("정렬 ("+ProductsSort.getAllValuesAsString()+")")
                ),
                responseFields(
                        fieldWithPath("pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                        fieldWithPath("pageSize").type(JsonFieldType.NUMBER).description("페이지당 개수"),
                        fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("총 아이템 수"),
                        fieldWithPath("items[]").type(JsonFieldType.ARRAY).description("상품 목록"),
                        fieldWithPath("items[].productUuid").type(JsonFieldType.STRING).description("상품 UUID"),
                        fieldWithPath("items[].name").type(JsonFieldType.STRING).description("상품 이름"),
                        fieldWithPath("items[].mainImgUrl").type(JsonFieldType.STRING).description("상품 메인 이미지 URL"),
                        fieldWithPath("items[].maxOrderQuantityPerOrder").type(JsonFieldType.NUMBER).description("1회 주문당 최대 주문 가능한 수량"),
                        fieldWithPath("items[].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                        fieldWithPath("items[].inventoryQuantity").type(JsonFieldType.NUMBER).description("상품 재고 수량")
                )
        );
    }
}
