package com.hcommerce.heecommerce.order;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class OrderControllerRestDocs {
    public static RestDocumentationResultHandler completeOrderReceipt() {
        return document("order-receipt-complete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("orderUuid").description("주문 ID")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                )
        );
    }

    public static RestDocumentationResultHandler placeOrder() {
        return document("place-order",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                // 주문자 정보
                fieldWithPath("ordererInfo.userId").type(JsonFieldType.NUMBER).description("주문자 USER ID"),
                fieldWithPath("ordererInfo.ordererName").type(JsonFieldType.STRING).description("주문자 이름"),
                fieldWithPath("ordererInfo.ordererPhoneNumber").type(JsonFieldType.STRING).description("주문자 휴대폰 번호"),

                // 받는 사람 정보
                fieldWithPath("recipientInfo.recipientName").type(JsonFieldType.STRING).description("받는 사람 이름"),
                fieldWithPath("recipientInfo.recipientPhoneNumber").type(JsonFieldType.STRING).description("받는 사람 휴대폰 번호"),
                fieldWithPath("recipientInfo.recipientAddress").type(JsonFieldType.STRING).description("받는 사람 주소"),
                fieldWithPath("recipientInfo.recipientDetailAddress").type(JsonFieldType.STRING).description("받는 사람 상세 주소"),
                fieldWithPath("recipientInfo.shippingRequest").type(JsonFieldType.STRING).description("배송 요청 사항"),

                // 결제 정보
                fieldWithPath("paymentInfo.dealProductUuid").type(JsonFieldType.STRING).description("딜 상품 UUID"),
                fieldWithPath("paymentInfo.dealProductTitle").type(JsonFieldType.STRING).description("딜 상품 타이틀"),
                fieldWithPath("paymentInfo.productUuid").type(JsonFieldType.STRING).description("상품 UUID"),
                fieldWithPath("paymentInfo.originPrice").type(JsonFieldType.NUMBER).description("원가격"),
                fieldWithPath("paymentInfo.discountAmount").type(JsonFieldType.NUMBER).description("할인 금액"),
                fieldWithPath("paymentInfo.orderQuantity").type(JsonFieldType.NUMBER).description("주문 수량"),
                fieldWithPath("paymentInfo.totalDealProductAmount").type(JsonFieldType.NUMBER).description("딜 상품 총 상품 금액"),
                fieldWithPath("paymentInfo.totalDiscountAmount").type(JsonFieldType.NUMBER).description("딜 상품 총 할인 금액"),
                fieldWithPath("paymentInfo.shippingFee").type(JsonFieldType.NUMBER).description("배송료"),
                fieldWithPath("paymentInfo.totalPaymentAmount").type(JsonFieldType.NUMBER).description("총 결제 금액"),
                fieldWithPath("paymentInfo.paymentType").type(JsonFieldType.STRING).description("결제 방법")
                ),
            responseFields(
                fieldWithPath("code").type(JsonFieldType.STRING).description("코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
            )
        );
    }
}
