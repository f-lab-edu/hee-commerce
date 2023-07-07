package com.hcommerce.heecommerce.order;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("OrderService")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderCommandRepository orderCommandRepository;

    @Mock
    private DealProductQueryRepository dealProductQueryRepository;

    @Mock
    private InventoryQueryRepository inventoryQueryRepository;

    @InjectMocks
    private OrderService orderService;

    private static final UUID TEMP_NOT_EXIST_UUID = UUID.fromString("8b455042-e709-11ed-93e5-0242ac110002");

    @Nested
    @DisplayName("completeOrderReceipt")
    class Describe_CompleteOrderReceipt {
        @Nested
        @DisplayName("with valid Uuid")
        class Context_With_Valid_Uuid {
            @Test
            @DisplayName("succeeds")
            void It_succeeds() {
                UUID uuid = UUID.randomUUID();

                given(orderCommandRepository.updateToCompleteOrderReceipt(uuid)).willReturn(uuid);

                // when
                orderService.completeOrderReceipt(uuid);

                // then
                verify(orderCommandRepository, times(1)).updateToCompleteOrderReceipt(uuid);
            }
        }

        @Nested
        @DisplayName("with invalid Uuid")
        class Context_With_Invalid_Uuid {
            @Test
            @DisplayName("throws OrderNotFoundException")
            void it_throws_OrderNotFoundException() {
                UUID uuid = UUID.randomUUID();

                given(orderCommandRepository.updateToCompleteOrderReceipt(TEMP_NOT_EXIST_UUID)).willReturn(null);

                // then
                assertThrows(OrderNotFoundException.class, () -> {
                    orderService.completeOrderReceipt(TEMP_NOT_EXIST_UUID);
                });
            }
        }
    }

    /**
     * any()를 사용하는 이유 : https://github.com/f-lab-edu/hee-commerce/issues/102 참고
     */
    @Nested
    @DisplayName("placeOrderInAdvance")
    class Describe_PlaceOrderInAdvance {
        @Nested
        @DisplayName("with valid orderForm")
        class Context_With_Valid_OrderForm {
            @Test
            @DisplayName("return OrderUuid")
            void It_returns_OrderUuid() {
                // given
                UUID dealProductUuid = UUID.randomUUID();

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
                    .dealProductUuid(dealProductUuid)
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                UUID expectedOrderUuid = UUID.randomUUID();

                given(orderCommandRepository.saveOrderInAdvance(any())).willReturn(expectedOrderUuid);

                boolean HAS_DEAL_PRODUCT_UUID = true;

                given(dealProductQueryRepository.hasDealProductUuid(dealProductUuid)).willReturn(HAS_DEAL_PRODUCT_UUID);

                given(inventoryQueryRepository.get(any())).willReturn(3);

                given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(3);

                // when
                UUID uuid = orderService.placeOrderInAdvance(orderForm);

                // then
                assertEquals(expectedOrderUuid, uuid);
            }
        }

        @Nested
        @DisplayName("with invalid dealProductUuid")
        class Context_With_Invalid_DealProductUuid {
            @Test
            @DisplayName("throws TimeDealProductNotFoundException")
            void It_throws_TimeDealProductNotFoundException() {
                // given
                UUID NOT_EXIST_DEAL_PRODUCT_UUID = UUID.randomUUID();

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
                    .dealProductUuid(NOT_EXIST_DEAL_PRODUCT_UUID)
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                given(dealProductQueryRepository.hasDealProductUuid(NOT_EXIST_DEAL_PRODUCT_UUID)).willReturn(false);

                // when + then
                assertThrows(TimeDealProductNotFoundException.class, () -> {
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
                    // given
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

                    given(dealProductQueryRepository.hasDealProductUuid(any())).willReturn(true);

                    given(inventoryQueryRepository.get(any())).willReturn(1);

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
                    // given
                    UUID uuid = UUID.randomUUID();

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
                        .outOfStockHandlingOption(OutOfStockHandlingOption.PARTIAL_ORDER)
                        .dealProductUuid(uuid)
                        .orderQuantity(2)
                        .paymentMethod(PaymentMethod.CREDIT_CARD)
                        .build();

                    given(dealProductQueryRepository.hasDealProductUuid(uuid)).willReturn(true);

                    given(inventoryQueryRepository.get(any())).willReturn(1);

                    given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(3);

                    // when + then
                    assertDoesNotThrow(() -> {
                        orderService.placeOrderInAdvance(orderForm);
                    });
                }
            }
        }

        @Nested
        @DisplayName("with orderQuantity > maxOrderQuantityPerOrder")
        class Context_With_OrderQuantity_Exceeds_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("throws MaxOrderQuantityExceededException")
            void It_throws_MaxOrderQuantityExceededException() {
                // given
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

                given(dealProductQueryRepository.hasDealProductUuid(any())).willReturn(true);

                given(inventoryQueryRepository.get(any())).willReturn(3);

                given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(1);

                // when + then
                assertThrows(MaxOrderQuantityExceededException.class, () -> {
                    orderService.placeOrderInAdvance(orderForm);
                });
            }
        }
    }
}
