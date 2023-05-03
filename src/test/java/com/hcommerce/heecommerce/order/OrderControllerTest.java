package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.EnableMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMockMvc
@SpringBootTest
@AutoConfigureRestDocs
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = new MockHttpSession();
    }

    @Nested
    @DisplayName("PATCH /admin/orders/{order_uuid}/order-receipt-complete ")
    class Describe_OrderReceiptComplete_API {
        private final String ORDER_RECEIPT_COMPLETE_URL = "/admin/orders/{order_uuid}/order-receipt-complete";
        private final UUID ORDER_UUID = UUID.randomUUID();

        @Nested
        @DisplayName("when login user is admin")
        class Context_When_Login_User_Is_Admin {
            @BeforeEach
            void setUp() {
                session.setAttribute("isAdmin", true);
            }

            @Nested
            @DisplayName("when `order_uuid` does not exist in DB")
            class Context_Without_Exited_OrderUuid {
                @Test
                @DisplayName("returns 404 error")
                void it_returns_404_Error() throws Exception {
                    String TEMP_NOT_EXIST_UUID = "8b455042-e709-11ed-93e5-0242ac110002";

                    // when
                    ResultActions resultActions = mockMvc.perform(
                            patch(ORDER_RECEIPT_COMPLETE_URL, TEMP_NOT_EXIST_UUID)
                                    .session(session)
                    );

                    // then
                    resultActions.andExpect(status().isOk())
                            .andExpect(content().string(containsString("해당 주문을 찾을 수 없습니다.")));
                }
            }

            @Nested
            @DisplayName("when `order_uuid` exists in DB")
            class Context_With_Exited_OrderUuid {
                @Nested
                @DisplayName("when order quantity < stock quantity")
                class Context_When_Order_Quantity_Is_Less_than_Stock_quantity {
                    @Test
                    @DisplayName("returns 200 ok")
                    void it_returns_200_OK() throws Exception {
                        // when
                        ResultActions resultActions = mockMvc.perform(
                                patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                                        .session(session)
                        );

                        // then
                        resultActions.andExpect(status().isOk())
                                .andExpect(content().string(containsString("주문 접수 완료가 처리되었습니다.")))
                                .andDo(OrderControllerRestDocs.completeOrderReceipt());
                    }
                }

                // TODO : 테스트 실패! ->
                @Nested
                @DisplayName("when order quantity > stock quantity")
                class Context_When_Order_Quantity_Is_More_than_Stock_quantity {
                    @Test
                    @DisplayName("returns 409 error")
                    void it_returns_409_Error() throws Exception {
                        // when
                        ResultActions resultActions = mockMvc.perform(
                                patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                                        .session(session)
                        );

                        // then
                        resultActions.andExpect(status().isConflict())
                                .andExpect(content().string(containsString("재고 수량 부족으로 주문 접수를 할 수 없습니다.")))
                                .andDo(OrderControllerRestDocs.completeOrderReceipt());
                    }
                }
            }
        }

        @Nested
        @DisplayName("when login user is not admin")
        class Context_When_Login_User_Is_Not_Admin {
            @Test
            @DisplayName("returns 403 error ")
            void it_returns_403_Error() throws Exception {
                // given
                session.setAttribute("isAdmin", false);

                // when
                ResultActions resultActions = mockMvc.perform(
                        patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                                .session(session)
                );

                // then
                resultActions.andExpect(status().isForbidden());
            }
        }

        @Nested
        @DisplayName("when user is not login")
        class Context_When_User_Is_Not_Login {
            @Test
            @DisplayName("returns 401 error ")
            void it_returns_401_Error() throws Exception {
                // when
                ResultActions resultActions = mockMvc.perform(
                        patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID)
                );

                // then
                resultActions.andExpect(status().isUnauthorized());
            }
        }
    }
}
