package com.hcommerce.heecommerce.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.EnableMockMvc;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.order.dto.OrderForm;
import com.hcommerce.heecommerce.order.dto.RecipientInfoForm;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
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
                given(orderService.placeOrderInAdvance(any())).willThrow(
                    TimeDealProductNotFoundException.class);

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
                    given(orderService.placeOrderInAdvance(any())).willThrow(
                        OrderOverStockException.class);

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
                        post("/orders/place-in-advance")
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
