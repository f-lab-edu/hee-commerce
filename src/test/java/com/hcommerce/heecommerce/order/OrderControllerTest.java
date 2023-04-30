package com.hcommerce.heecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 접수 완료")
    void completeOrderReceipt() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin/orders/{order_uuid}/order-receipt-complete", UUID.randomUUID())
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(OrderControllerRestDocs.completeOrderReceipt());
    }

    @Test
    @DisplayName("UUID 형식에 어긋난 order_uuid로 요청한 경우")
    void completeOrderReceiptWithOrderUuidIsNotUuidType() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin/orders/{order_uuid}/order-receipt-complete", 123)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("UUID 형식에 맞지만, DB에 없는 order_uuid로 요청한 경우")
    void completeOrderReceiptWithOrderUuidIsNotInDB() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/admin/orders/{order_uuid}/order-receipt-complete", 123)
        );

        // then
        resultActions.andExpect(status().isNotFound());
    }
}
