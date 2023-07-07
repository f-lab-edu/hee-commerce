package com.hcommerce.heecommerce.order;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private OrderService orderService;

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
        @DisplayName("with orderQuantity < inventory")
        class Context_When_Order_Is_Successful {
            @Test
            @DisplayName("returns 201")
            void It_returns_201() throws Exception {
                // when
                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .dealProductUuid(UUID.randomUUID())
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
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

        @Nested
        @DisplayName("with orderQuantity > inventory and outOfStockHandlingOption is `PARTIAL_ORDER`")
        class Context_With_OrderQuantity_Exceeds_Inventory_And_OutOfStockHandlingOption_Is_PARTIAL_ORDER {
            @Test
            @DisplayName("returns 201")
            void It_returns_201() throws Exception {
                // given
                UUID UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY = UUID.randomUUID();

                int ORDER_QUANTITY_EXCEEDING_INVENTORY = 5;

                OutOfStockHandlingOption PARTIAL_ORDER = OutOfStockHandlingOption.PARTIAL_ORDER;

                // when
                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(PARTIAL_ORDER)
                    .dealProductUuid(UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .orderQuantity(ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                String content = objectMapper.writeValueAsString(orderForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isCreated());
            }
        }

        @Nested
        @DisplayName("with orderQuantity > inventory and outOfStockHandlingOption is `ALL_CANCEL`")
        class Context_With_OrderQuantity_Exceeds_Inventory_And_OutOfStockHandlingOption_Is_ALL_CANCEL {
            @Test
            @DisplayName("returns 409 error")
            void It_returns_409() throws Exception {
                // given
                UUID UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY = UUID.randomUUID();

                int ORDER_QUANTITY_EXCEEDING_INVENTORY = 5;

                OutOfStockHandlingOption ALL_CANCEL = OutOfStockHandlingOption.ALL_CANCEL;

                // when
                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(ALL_CANCEL)
                    .dealProductUuid(UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .orderQuantity(ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                String content = objectMapper.writeValueAsString(orderForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isConflict());
            }
        }

        @Nested
        @DisplayName("with orderQuantity > maxOrderQuantityPerOrder")
        class Context_With_OrderQuantity_Exceeds_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("returns 400 error")
            void It_returns_400() throws Exception {
                // given
                UUID UUID_WITH_MAX_ORDER_QUANTITY_PER_ORDER_OF_10 = UUID.randomUUID();

                int ORDER_QUANTITY_EXCEEDING_MAX_ORDER_QUANTITY_PER_ORDER = 12;

                // when
                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .dealProductUuid(UUID_WITH_MAX_ORDER_QUANTITY_PER_ORDER_OF_10)
                    .orderQuantity(ORDER_QUANTITY_EXCEEDING_MAX_ORDER_QUANTITY_PER_ORDER)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                String content = objectMapper.writeValueAsString(orderForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("POST /orders/place-in-advance")
    class Describe_PlaceOrderInAdvance_API {
        @Nested
        @DisplayName("with valid orderForm")
        class Context_With_Valid_OrderForm {
            @Test
            @DisplayName("returns 201")
            void It_returns_201() throws Exception {
                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .dealProductUuid(UUID.randomUUID())
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                // given
                given(orderService.placeOrderInAdvance(any())).willReturn(UUID.randomUUID());

                // when
                String content = objectMapper.writeValueAsString(orderForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isCreated())
                    .andDo(OrderControllerRestDocs.placeOrderInAdvance());
            }
        }

        @Nested
        @DisplayName("with invalid dealProductUuid")
        class Context_With_Invalid_DealProductUuid {
            @Test
            @DisplayName("returns 404")
            void It_returns_404() throws Exception {

                UUID NOT_EXIST_DEAL_PRODUCT_UUID = UUID.randomUUID();

                OrderForm orderFormWithNotExistDealProductUuid = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .dealProductUuid(NOT_EXIST_DEAL_PRODUCT_UUID)
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                // given
                given(orderService.placeOrderInAdvance(any())).willThrow(TimeDealProductNotFoundException.class);

                // when
                String content = objectMapper.writeValueAsString(orderFormWithNotExistDealProductUuid);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isNotFound());
            }
        }

        @Nested
        @DisplayName("with orderQuantity > inventory")
        class Context_With_OrderQuantity_Exceeds_Inventory {
            @Nested
            @DisplayName("with outOfStockHandlingOption is ALL_CANCEL")
            class Context_With_outOfStockHandlingOption_Is_ALL_CANCEL {
                @Test
                @DisplayName("returns 409 error")
                void It_returns_409_Error() throws Exception {
                    // given
                    given(orderService.placeOrderInAdvance(any())).willThrow(OrderOverStockException.class);

                    UUID UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY = UUID.randomUUID();

                    int ORDER_QUANTITY_EXCEEDING_INVENTORY = 5;

                    OutOfStockHandlingOption ALL_CANCEL = OutOfStockHandlingOption.ALL_CANCEL;

                    OrderForm orderForm = OrderForm.builder()
                        .userId(1)
                        .orderUuid(UUID.randomUUID())
                        .recipientInfoForm(
                            RecipientInfoForm.builder()
                                .recipientName("leecommerce")
                                .recipientPhoneNumber("01087654321")
                                .recipientAddress("서울시 ")
                                .recipientDetailAddress("101호")
                                .shippingRequest("빠른 배송 부탁드려요!")
                                .build()
                        )
                        .outOfStockHandlingOption(ALL_CANCEL)
                        .dealProductUuid(UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY)
                        .orderQuantity(ORDER_QUANTITY_EXCEEDING_INVENTORY)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .build();

                    String content = objectMapper.writeValueAsString(orderForm);

                    // when
                    ResultActions resultActions = mockMvc.perform(
                        post("/orders/place-in-advance")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    );

                    // then
                    resultActions.andExpect(status().isConflict());
                }
            }

            @Nested
            @DisplayName("with outOfStockHandlingOption is PARTIAL_ORDER")
            class Context_With_outOfStockHandlingOption_Is_PARTIAL_ORDER {
                @Test
                @DisplayName("returns 201")
                void It_returns_201() throws Exception {
                    // given
                    UUID UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY = UUID.randomUUID();

                    int ORDER_QUANTITY_EXCEEDING_INVENTORY = 5;

                    OutOfStockHandlingOption PARTIAL_ORDER = OutOfStockHandlingOption.PARTIAL_ORDER;

                    // when
                    OrderForm orderForm = OrderForm.builder()
                        .userId(1)
                        .orderUuid(UUID.randomUUID())
                        .recipientInfoForm(
                            RecipientInfoForm.builder()
                                .recipientName("leecommerce")
                                .recipientPhoneNumber("01087654321")
                                .recipientAddress("서울시 ")
                                .recipientDetailAddress("101호")
                                .shippingRequest("빠른 배송 부탁드려요!")
                                .build()
                        )
                        .outOfStockHandlingOption(PARTIAL_ORDER)
                        .dealProductUuid(UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY)
                        .orderQuantity(ORDER_QUANTITY_EXCEEDING_INVENTORY)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .build();

                    given(orderService.placeOrderInAdvance(orderForm)).willReturn(UUID.randomUUID());

                    String content = objectMapper.writeValueAsString(orderForm);

                    ResultActions resultActions = mockMvc.perform(
                        post("/orders")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    );

                    // then
                    resultActions.andExpect(status().isCreated());
                }
            }
        }


        @Nested
        @DisplayName("with orderQuantity > maxOrderQuantityPerOrder")
        class Context_With_OrderQuantity_Exceeds_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("returns 409 error")
            void It_returns_409_Error() throws Exception {
                // given
                given(orderService.placeOrderInAdvance(any())).willThrow(OrderOverStockException.class);

                UUID UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY = UUID.randomUUID();

                int ORDER_QUANTITY_EXCEEDING_INVENTORY = 5;

                OutOfStockHandlingOption ALL_CANCEL = OutOfStockHandlingOption.ALL_CANCEL;

                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
                    .orderUuid(UUID.randomUUID())
                    .recipientInfoForm(
                        RecipientInfoForm.builder()
                            .recipientName("leecommerce")
                            .recipientPhoneNumber("01087654321")
                            .recipientAddress("서울시 ")
                            .recipientDetailAddress("101호")
                            .shippingRequest("빠른 배송 부탁드려요!")
                            .build()
                    )
                    .outOfStockHandlingOption(ALL_CANCEL)
                    .dealProductUuid(UUID_WITH_ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .orderQuantity(ORDER_QUANTITY_EXCEEDING_INVENTORY)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                String content = objectMapper.writeValueAsString(orderForm);

                // when
                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isConflict());
            }
        }
    }

    @Nested
    @DisplayName("POST /orders/approve")
    class Describe_ApproveOrder_API {
        @Test
        @DisplayName("returns 201")
        void It_returns_201() throws Exception {
            OrderApproveForm orderForm = OrderApproveForm.builder()
                .paymentKey("5zJ4xY7m0kODnyRpQWGrN2xqGlNvLrKwv1M9ENjbeoPaZdL6")
                .orderId(UUID.randomUUID().toString())
                .amount(15000)
                .build();

            // given
            given(orderService.approveOrder(any())).willReturn(UUID.randomUUID());

            // when
            String content = objectMapper.writeValueAsString(orderForm);

            ResultActions resultActions = mockMvc.perform(
                post("/orders/approve")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
            );

            // then
            resultActions.andExpect(status().isCreated())
                .andDo(OrderControllerRestDocs.approveOrder());

        }
    }
}
