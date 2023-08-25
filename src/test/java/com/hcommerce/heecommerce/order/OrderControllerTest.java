package com.hcommerce.heecommerce.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.EnableMockMvc;
import com.hcommerce.heecommerce.auth.AuthenticationService;
import com.hcommerce.heecommerce.fixture.AuthFixture;
import com.hcommerce.heecommerce.fixture.OrderFixture;
import com.hcommerce.heecommerce.order.domain.OrderForm;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.order.dto.OrderFormDto;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.exception.InvalidPaymentAmountException;
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

    private AuthenticationService authenticationService;

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
        class Context_With_Valid_OrderFormDto {
            @Test
            @DisplayName("returns 201")
            void It_returns_201() throws Exception {
                // given
                given(orderService.placeOrderInAdvance(any())).willReturn(UUID.randomUUID());

                OrderFormDto orderFormDto = OrderFixture.ORDER_FORM_DTO;

                // when
                String content = objectMapper.writeValueAsString(orderFormDto);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isCreated())
                    .andDo(OrderControllerRestDocs.placeOrderInAdvance());
            }
        }

        @Nested
        @DisplayName("with invalid Authorization")
        class Context_With_Invalid_Authorization {
            @Test
            @DisplayName("returns 401")
            void It_returns_401() throws Exception {

                // given
                String authorization = AuthFixture.AUTHORIZATION_WITH_INVALID_AUTH_TYPE;

                OrderFormDto orderFormDto = OrderFixture.ORDER_FORM_DTO;

                // when
                String content = objectMapper.writeValueAsString(orderFormDto);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("with invalid token")
        class Context_With_Invalid_token {
            @Test
            @DisplayName("returns 401")
            void It_returns_401() throws Exception {

                // given
                String authorization = AuthFixture.AUTHORIZATION_WITHOUT_TOKEN;

                OrderFormDto orderFormDto = OrderFixture.ORDER_FORM_DTO;

                // when
                String content = objectMapper.writeValueAsString(orderFormDto);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isUnauthorized());
            }
        }

        @Nested
        @DisplayName("with invalid dealProductUuid")
        class Context_With_Invalid_DealProductUuid {
            @Test
            @DisplayName("returns 404")
            void It_returns_404() throws Exception {

                UUID NOT_EXIST_DEAL_PRODUCT_UUID = UUID.randomUUID();

                OrderFormDto orderFormDtoWithNotExistDealProductUuid = OrderFixture.rebuilder()
                    .dealProductUuid(NOT_EXIST_DEAL_PRODUCT_UUID)
                    .build();

                // given
                given(orderService.placeOrderInAdvance(any())).willThrow(
                    TimeDealProductNotFoundException.class);

                // when
                String content = objectMapper.writeValueAsString(
                    orderFormDtoWithNotExistDealProductUuid);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isNotFound());
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

                OrderFormDto orderFormDto = OrderFixture.ORDER_FORM_DTO;;

                String content = objectMapper.writeValueAsString(orderFormDto);

                // when
                ResultActions resultActions = mockMvc.perform(
                    post("/orders/place-in-advance")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isConflict());
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

                    OrderFormDto orderFormDto = OrderFixture.rebuilder()
                                                .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                                                .build();

                    String content = objectMapper.writeValueAsString(orderFormDto);

                    // when
                    ResultActions resultActions = mockMvc.perform(
                        post("/orders/place-in-advance")
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", AuthFixture.AUTHORIZATION)
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
                    OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                                                .outOfStockHandlingOption(OutOfStockHandlingOption.PARTIAL_ORDER)
                                                .build();


                    given(orderService.placeOrderInAdvance(orderForm)).willReturn(UUID.randomUUID());

                    String content = objectMapper.writeValueAsString(orderForm);

                    // when
                    ResultActions resultActions = mockMvc.perform(
                        post("/orders/place-in-advance")
                            .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    );

                    // then
                    resultActions.andExpect(status().isCreated());
                }
            }
        }

    }

    @Nested
    @DisplayName("POST /orders/approve")
    class Describe_ApproveOrder_API {
        @Nested
        @DisplayName("with valid orderApproveForm")
        class Context_With_valid_orderApproveForm {
            @Test
            @DisplayName("returns 201")
            void It_returns_201() throws Exception {
                // given
                given(orderService.approveOrder(any())).willReturn(UUID.randomUUID());

                // when
                OrderApproveForm orderApproveForm = OrderFixture.orderApproveForm;

                String content = objectMapper.writeValueAsString(orderApproveForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/approve")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isCreated())
                    .andDo(OrderControllerRestDocs.approveOrder());
            }
        }

        @Nested
        @DisplayName("with invalid amount")
        class Context_With_Invalid_Amount {
            @Test
            @DisplayName("returns 400 error")
            void It_Returns_400_error() throws Exception {
                // given
                given(orderService.approveOrder(any())).willThrow(InvalidPaymentAmountException.class);

                // when
                OrderApproveForm orderApproveForm = OrderFixture.orderApproveForm;

                String content = objectMapper.writeValueAsString(orderApproveForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/approve")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("with invalid realOrderQuantity")
        class Context_With_Invalid_RealOrderQuantity {
            @Test
            @DisplayName("returns 400 error")
            void It_Returns_400_error() throws Exception {
                // given
                given(orderService.approveOrder(any())).willThrow(OrderOverStockException.class);

                // when
                OrderApproveForm orderApproveForm = OrderFixture.orderApproveForm;

                String content = objectMapper.writeValueAsString(orderApproveForm);

                ResultActions resultActions = mockMvc.perform(
                    post("/orders/approve")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", AuthFixture.AUTHORIZATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                );

                // then
                resultActions.andExpect(status().isConflict());
            }
        }
    }
}
