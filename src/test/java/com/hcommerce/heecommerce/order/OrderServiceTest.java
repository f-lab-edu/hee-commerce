package com.hcommerce.heecommerce.order;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.common.utils.TosspaymentsUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.fixture.DealProductFixture;
import com.hcommerce.heecommerce.fixture.OrderFixture;
import com.hcommerce.heecommerce.fixture.TossConfirmResponse;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import com.hcommerce.heecommerce.order.domain.OrderForm;
import com.hcommerce.heecommerce.order.dto.OrderApproveForm;
import com.hcommerce.heecommerce.order.dto.OrderForOrderApproveValidationDto;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.exception.InvalidPaymentAmountException;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import com.hcommerce.heecommerce.user.exception.UserNotFoundException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@DisplayName("OrderService")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private OrderQueryRepository orderQueryRepository;

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @Mock
    private DealProductQueryRepository dealProductQueryRepository;

    @Mock
    private InventoryQueryRepository inventoryQueryRepository;

    @Mock
    private InventoryCommandRepository inventoryCommandRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    private static final UUID TEMP_NOT_EXIST_UUID = UUID.fromString("8b455042-e709-11ed-93e5-0242ac110002");

    private Instant STARTED_AT = Instant.now();

    private Instant FINISHED_AT = Instant.now().plus(1, ChronoUnit.HOURS);

    /**
     * any()를 사용하는 이유 : https://github.com/f-lab-edu/hee-commerce/issues/102 참고
     */
    @Nested
    @DisplayName("placeOrderInAdvance")
    class Describe_PlaceOrderInAdvance {
        @Nested
        @DisplayName("with valid orderForm")
        class Context_With_Valid_OrderFormDto {
            @Test
            @DisplayName("return OrderUuid")
            void It_returns_OrderUuid() throws InterruptedException {
                // given
                given_with_valid_dealProductUuid();

                given_with_valid_userId();

                given_with_maxOrderQuantityPerOrder(OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER);

                // 실제 주문 수량 계산
                given_when_determineRealOrderQuantity_is_success();

                // 주문 내역 미리 저장
                UUID expectedOrderUuid = UUID.randomUUID();

                given_when_saveOrderInAdvance_is_success(expectedOrderUuid);

                // when
                UUID actualUuid = orderService.placeOrderInAdvance(OrderFixture.ORDER_FORM);

                // then
                assertEquals(expectedOrderUuid, actualUuid);
            }
        }

        @Nested
        @DisplayName("with invalid dealProductUuid")
        class Context_With_Invalid_DealProductUuid {
            @Test
            @DisplayName("throws TimeDealProductNotFoundException")
            void It_throws_TimeDealProductNotFoundException() {
                // given
                given_with_invalid_dealProductUuid();

                // when + then
                assertThrows(TimeDealProductNotFoundException.class, () -> {
                    orderService.placeOrderInAdvance(OrderFixture.ORDER_FORM);
                });
            }
        }

        @Nested
        @DisplayName("with invalid userId")
        class Context_With_Invalid_UserId {
            @Test
            @DisplayName("throws UserNotFoundException")
            void It_throws_UserNotFoundException() {
                // given
                given_with_valid_dealProductUuid();

                given_with_invalid_userId();

                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                                            .userId(0)
                                            .build();

                // when + then
                assertThrows(UserNotFoundException.class, () -> {
                    orderService.placeOrderInAdvance(orderForm);
                });
            }
        }


        @Nested
        @DisplayName("with orderQuantity > maxOrderQuantityPerOrder")
        class Context_With_OrderQuantity_Exceeds_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("throws MaxOrderQuantityExceededException")
            void It_throws_MaxOrderQuantityExceededException() {
                // given
                given_with_valid_dealProductUuid();

                given_with_valid_userId();

                given_with_maxOrderQuantityPerOrder(OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER);

                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                                            .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_MAX_ORDER_QUANTITY_PER_ORDER)
                                            .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                                            .build();

                // when + then
                assertThrows(MaxOrderQuantityExceededException.class, () -> {
                    orderService.placeOrderInAdvance(orderForm);
                });
            }
        }

        @Nested
        @DisplayName("with orderQuantity > inventory")
        class Context_With_OrderQuantity_Exceeds_Inventory {
            @Nested
            @DisplayName("with outOfStockHandlingOption is ALL_CANCEL")
            class Context_With_outOfStockHandlingOption_Is_ALL_CANCEL {
                @Test
                @DisplayName("throws OrderOverStockException")
                void It_throws_OrderOverStockException() {
                    given_with_valid_dealProductUuid();

                    given_with_valid_userId();

                    given_with_maxOrderQuantityPerOrder(OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER);

                    given_with_inventory(OrderFixture.INVENTORY);

                    OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                                                .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_INVENTORY)
                                                .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                                                .build();

                    // when + then
                    assertThrows(OrderOverStockException.class, () -> {
                        orderService.placeOrderInAdvance(orderForm);
                    });
                }
            }

            @Nested
            @DisplayName("with outOfStockHandlingOption is PARTIAL_ORDER")
            class Context_With_outOfStockHandlingOption_Is_PARTIAL_ORDER {
                @Test
                @DisplayName("does not throws OrderOverStockException")
                void It_Does_Not_OrderOverStockException() {
                    given_with_valid_dealProductUuid();

                    given_with_valid_userId();

                    given_with_maxOrderQuantityPerOrder(OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER);

                    given_with_inventory(OrderFixture.INVENTORY);

                    UUID uuidFixture = UUID.randomUUID();

                    given_when_saveOrderInAdvance_is_success(uuidFixture);

                    OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                                                .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_INVENTORY)
                                                .outOfStockHandlingOption(OutOfStockHandlingOption.PARTIAL_ORDER)
                                                .build();

                    // when + then
                    assertDoesNotThrow(() -> {
                        orderService.placeOrderInAdvance(orderForm);
                    });
                }
            }
        }

//        @Nested
//        @DisplayName("when Invalid inventory decrease occurs")
//        class Context_With_Invalid_Inventory_Decrease_Occurs {
//            @Test
//            @DisplayName("rollbacks inventory and throws OrderOverStockException")
//            void It_Rollbacks_inventory_And_throws_OrderOverStockException() {
//                // given
//                given_with_valid_dealProductUuid();
//
//                given_with_valid_userId();
//
//                given_with_maxOrderQuantityPerOrder(OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER);
//
//                given_with_inventory(OrderFixture.INVENTORY);
//
////                given_with_inventory_after_decrease(OrderFixture.INVALID_INVENTORY_AFTER_DECREASE);
//
//                UUID ROLLBACK_NEEDED_DEAL_PRODUCT_UUID = UUID.randomUUID();
//
//                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
//                                            .dealProductUuid(ROLLBACK_NEEDED_DEAL_PRODUCT_UUID)
//                                            .build();
//
//                // when
//                assertThrows(OrderOverStockException.class, () -> {
//                    orderService.placeOrderInAdvance(orderForm);
//                });
//
//                verify(inventoryCommandRepository).increase(any());
//            }
//        }

        private void given_with_valid_dealProductUuid() {
            given(dealProductQueryRepository.hasDealProductUuid(any())).willReturn(true);
        }

        private void given_with_invalid_dealProductUuid() {
            given(dealProductQueryRepository.hasDealProductUuid(any())).willReturn(false);
        }

        private void given_with_valid_userId() {
            given(userQueryRepository.hasUserId(anyInt())).willReturn(true);
        }

        private void given_with_invalid_userId() {
            given(userQueryRepository.hasUserId(anyInt())).willReturn(false);
        }

        private void given_with_maxOrderQuantityPerOrder(int maxOrderQuantityPerOrder) {
            given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(maxOrderQuantityPerOrder);
        }

        private void given_when_determineRealOrderQuantity_is_success() {
            given_with_inventory(OrderFixture.INVENTORY);
        }

        private void given_with_inventory(int inventory) {
            given(inventoryQueryRepository.get(any())).willReturn(inventory);
        }

        private void given_when_saveOrderInAdvance_is_success(UUID orderUuid) {
            given(dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(any())).willReturn(DealProductFixture.timeDealProductDetail);

            given(orderCommandRepository.saveOrderInAdvance(any())).willReturn(orderUuid);
        }
    }

    @Nested
    @DisplayName("approveOrder")
    class Describe_ApproveOrder {
        @Nested
        @DisplayName("with valid orderApproveForm")
        class Context_With_valid_orderApproveForm {
            @Test
            @DisplayName("returns orderUuid")
            void It_returns_orderUuid() {
                // given
                OrderApproveForm orderApproveForm = OrderFixture.orderApproveForm;

                OrderForOrderApproveValidationDto orderForOrderApproveValidationDto = OrderFixture.orderForOrderApproveValidationDto;

                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(orderForOrderApproveValidationDto);

                HttpEntity<String> request = TosspaymentsUtils.createHttpRequestForPaymentApprove(orderApproveForm);

                JsonNode jsonNode = new ObjectMapper().convertValue(TossConfirmResponse.of(), JsonNode.class);

                given(restTemplate.postForEntity(TosspaymentsUtils.TOSS_PAYMENT_CONFIRM_URL, request, JsonNode.class)).willReturn(new ResponseEntity<>(jsonNode, HttpStatus.CREATED));

                given(inventoryQueryRepository.get(any())).willReturn(OrderFixture.INVENTORY);

                // when + then
                UUID uuid = orderService.approveOrder(orderApproveForm);

                assertEquals(uuid.toString(), orderApproveForm.getOrderId());
            }
        }

        @Nested
        @DisplayName("with invalid amount")
        class Context_With_Invalid_Amount {
            @Test
            @DisplayName("throws InvalidPaymentAmountException")
            void It_throws_InvalidPaymentAmountException() {
                // given
                OrderApproveForm orderApproveFormWithInValidAmount =  OrderFixture.orderApproveFormRebuilder()
                                                                                .amount(OrderFixture.INVALID_AMOUNT)
                                                                                .build();

                OrderForOrderApproveValidationDto orderForOrderApproveValidationDto = OrderFixture.orderForOrderApproveValidationDto;

                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(orderForOrderApproveValidationDto);

                // when + then
                assertThrows(InvalidPaymentAmountException.class, () -> {
                    orderService.approveOrder(orderApproveFormWithInValidAmount);
                });
            }
        }

        @Nested
        @DisplayName("with invalid realOrderQuantity")
        class Context_With_Invalid_RealOrderQuantity {
            @Test
            @DisplayName("throws OrderOverStockException")
            void It_throws_OrderOverStockException() {
                // given
                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(OrderFixture.orderForOrderApproveValidationDto);

                HttpEntity<String> request = TosspaymentsUtils.createHttpRequestForPaymentApprove(OrderFixture.orderApproveForm);

                JsonNode jsonNode = new ObjectMapper().convertValue(TossConfirmResponse.of(), JsonNode.class);

                given(restTemplate.postForEntity(TosspaymentsUtils.TOSS_PAYMENT_CONFIRM_URL, request, JsonNode.class)).willReturn(new ResponseEntity<>(jsonNode, HttpStatus.CREATED));

                given(inventoryQueryRepository.get(any())).willReturn(0);

                // when + then
                assertThrows(OrderOverStockException.class, () -> {
                    orderService.approveOrder(OrderFixture.orderApproveForm);
                });
            }
        }

        @Nested
        @DisplayName("when Invalid inventory decrease occurs")
        class Context_With_Invalid_Inventory_Decrease_Occurs {
            @Test
            @DisplayName("rollbacks inventory and throws OrderOverStockException")
            void It_Rollbacks_inventory_And_throws_OrderOverStockException() {
                // given
                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(OrderFixture.orderForOrderApproveValidationDto);

                HttpEntity<String> request = TosspaymentsUtils.createHttpRequestForPaymentApprove(OrderFixture.orderApproveForm);

                JsonNode jsonNode = new ObjectMapper().convertValue(TossConfirmResponse.of(), JsonNode.class);

                given(restTemplate.postForEntity(TosspaymentsUtils.TOSS_PAYMENT_CONFIRM_URL, request, JsonNode.class)).willReturn(new ResponseEntity<>(jsonNode, HttpStatus.CREATED));

                given(inventoryQueryRepository.get(any())).willReturn(OrderFixture.INVENTORY);

                given(inventoryCommandRepository.decrease(any())).willReturn(OrderFixture.INVALID_INVENTORY_AFTER_DECREASE);

                // when + then
                assertThrows(OrderOverStockException.class, () -> {
                    orderService.approveOrder(OrderFixture.orderApproveForm);
                });

                verify(inventoryCommandRepository).increase(any());
            }
        }
    }
}
