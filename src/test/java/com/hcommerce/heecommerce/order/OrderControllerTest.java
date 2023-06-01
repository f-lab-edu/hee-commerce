package com.hcommerce.heecommerce.order;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.EnableMockMvc;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@EnableMockMvc
@SpringBootTest
@AutoConfigureRestDocs
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        session = new MockHttpSession();
    }

    // TODO : placeOrder 완성되면 삭제할 예정. 예외처리 참고용으로 남겨 둠
    @Nested
    @DisplayName("PATCH /admin/orders/{orderUuid}/order-receipt-complete ")
    class Describe_OrderReceiptComplete_API {
        private final String ORDER_RECEIPT_COMPLETE_URL = "/admin/orders/{orderUuid}/order-receipt-complete";

        private final UUID ORDER_UUID = UUID.randomUUID();

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Nested
        @DisplayName("when login user is admin")
        class Context_When_Login_User_Is_Admin {
            @BeforeEach
            void setUp() {
                session.setAttribute("isAdmin", true);
            }

            @Nested
            @DisplayName("when `orderUuid` exists in DB")
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

                // TODO : DB 연동 후 테스트 어떻게 수정할지 고민하기
                @Nested
                @DisplayName("when order quantity > stock quantity")
                class Context_When_Order_Quantity_Is_More_than_Stock_quantity {
                    @Test
                    @DisplayName("returns 409 error")
                    void it_returns_409_Error() throws Exception {
                        // given
                        final String ORDER_UUID_OVER_STOCK = "27f43b31-e97f-11ed-93e5-0242ac110002";

                        // when
                        ResultActions resultActions = mockMvc.perform(
                                patch(ORDER_RECEIPT_COMPLETE_URL, ORDER_UUID_OVER_STOCK)
                                        .session(session)
                        );

                        // then
                        resultActions.andExpect(status().isConflict())
                                .andExpect(content().string(containsString("재고 수량 부족으로 주문 접수를 할 수 없습니다.")));
                    }
                }
            }

            @Nested
            @DisplayName("when `orderUuid` does not exist in DB")
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
                    resultActions.andExpect(status().isNotFound())
                            .andExpect(content().string(containsString("해당 주문을 찾을 수 없습니다.")));
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
                resultActions.andExpect(status().isForbidden())
                        .andExpect(content().string(containsString("관리자만 이용 가능합니다.")));
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
                resultActions.andExpect(status().isUnauthorized())
                        .andExpect(content().string(containsString("로그인 후에 이용할 수 있습니다.")));

            }
        }
    }

    @Nested
    @DisplayName("POST /orders")
    class Describe_PlaceOrder_API {
        @Nested
        @DisplayName("when order is successful")
        class Context_When_Order_Is_Successful {
            @Test
            @DisplayName("returns 201")
            void it_returns_201() throws Exception {
                // when
                OrderForm orderForm = OrderForm.builder()
                    .ordererInfo(
                        OrdererInfo.builder()
                            .userId(1)
                            .ordererName("kimcommerce")
                            .ordererPhoneNumber("01012345678")
                            .build()
                    )
                    .recipientInfo(
                        RecipientInfo.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .paymentInfo(
                        PaymentInfo.builder()
                            .dealProductUuid(UUID.randomUUID())
                            .dealProductTitle("[무료배송] 초특가 사과 1상자")
                            .productUuid(UUID.randomUUID())
                            .originPrice(10000)
                            .discountAmount(2000)
                            .orderQuantity(2)
                            .totalDealProductAmount(20000)
                            .totalDiscountAmount(4000)
                            .shippingFee(0)
                            .totalPaymentAmount(16000)
                            .paymentType(PaymentType.CREDIT_CARD)
                            .build()
                    )
                    .build();

                String content = objectMapper.writeValueAsString(orderForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isCreated())
                    .andDo(OrderControllerRestDocs.placeOrder());
            }
        }
    }
}
