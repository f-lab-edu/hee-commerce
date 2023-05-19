package com.hcommerce.heecommerce.deal;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import com.hcommerce.heecommerce.product.ProductsSort;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
public class DealControllerRestDocs {

    public static RestDocumentationResultHandler getDealProducts() {
        return document("get-deal-products",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("pageNumber").optional().description("페이지 번호"),
                        parameterWithName("sort").optional().description("정렬 ("+ ProductsSort.getAllValuesAsString()+")")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                        fieldWithPath("data.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                        fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER).description("페이지당 개수"),
                        fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("총 아이템 수"),
                        fieldWithPath("data.items[]").type(JsonFieldType.ARRAY).description("딜 상품 목록"),
                        fieldWithPath("data.items[].productUuid").type(JsonFieldType.STRING).description("상품 UUID"),
                        fieldWithPath("data.items[].dealProductTitle").type(JsonFieldType.STRING).description("딜 상품 타이틀"),
                        fieldWithPath("data.items[].mainImgThumbnailUrl").type(JsonFieldType.STRING).description("딜 상품 메인 이미지 썸네일 URL"),
                        fieldWithPath("data.items[].originPrice").type(JsonFieldType.NUMBER).description("상품 원가격"),
                        fieldWithPath("data.items[].discountType").type(JsonFieldType.STRING).description("할인 유형"),
                        fieldWithPath("data.items[].discountValue").type(JsonFieldType.NUMBER).description("할인가"),
                        fieldWithPath("data.items[].dealQuantity").type(JsonFieldType.NUMBER).description("상품 재고 수량"),
                        fieldWithPath("data.items[].maxDealOrderQuantityPerOrder").type(JsonFieldType.NUMBER).description("1회 주문당 최대 주문 가능한 수량")
                )
        );
    }
}
