package com.hcommerce.heecommerce.order;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.deal.DealProductQueryRepository;
import com.hcommerce.heecommerce.deal.DiscountType;
import com.hcommerce.heecommerce.deal.TimeDealProductDetail;
import com.hcommerce.heecommerce.inventory.InventoryCommandRepository;
import com.hcommerce.heecommerce.inventory.InventoryQueryRepository;
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
import org.springframework.data.redis.core.RedisTemplate;

@DisplayName("OrderService")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

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

                given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(3);

                TimeDealProductDetail timeDealProductDetail = TimeDealProductDetail.builder()
                    .dealProductUuid(dealProductUuid)
                    .dealProductTile("1000원 할인 상품 1")
                    .productMainImgUrl("/test.png")
                    .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
                    .productOriginPrice(3000)
                    .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                    .dealProductDiscountValue(1000)
                    .dealProductDealQuantity(3)
                    .maxOrderQuantityPerOrder(10)
                    .startedAt(STARTED_AT)
                    .finishedAt(FINISHED_AT)
                    .build();

                given(dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(any())).willReturn(timeDealProductDetail);

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
                void It_throws_OrderOverStockException() { // TODO : 테스트 코드 깨짐. redisTemplate SessionCallback Mocking 하는 방법 또는 다른 방법을 찾은 후 해결하기
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

                    given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(3);

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

                    TimeDealProductDetail timeDealProductDetail = TimeDealProductDetail.builder()
                        .dealProductUuid(uuid)
                        .dealProductTile("1000원 할인 상품 1")
                        .productMainImgUrl("/test.png")
                        .productDetailImgUrls(new String[]{"/detail_test1.png", "/detail_test2.png", "/detail_test3.png", "/detail_test4.png", "/detail_test5.png"})
                        .productOriginPrice(3000)
                        .dealProductDiscountType(DiscountType.FIXED_AMOUNT)
                        .dealProductDiscountValue(1000)
                        .dealProductDealQuantity(3)
                        .maxOrderQuantityPerOrder(10)
                        .startedAt(STARTED_AT)
                        .finishedAt(FINISHED_AT)
                        .build();

                    given(dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(any())).willReturn(timeDealProductDetail);

                    given(dealProductQueryRepository.hasDealProductUuid(uuid)).willReturn(true);

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

                given(dealProductQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(any())).willReturn(1);

                // when + then
                assertThrows(MaxOrderQuantityExceededException.class, () -> {
                    orderService.placeOrderInAdvance(orderForm);
                });
            }
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
                OrderApproveForm orderApproveForm = OrderApproveForm.builder()
                    .orderId(UUID.randomUUID().toString())
                    .amount(1000)
                    .paymentKey("tossPaymentsPaymentKey")
                    .build();

                OrderEntityForOrderApproveValidation orderEntityForOrderApproveValidation =
                    OrderEntityForOrderApproveValidation.builder()
                        .orderQuantity(3)
                        .totalPaymentAmount(1000)
                        .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                        .dealProductUuid(TypeConversionUtils.convertUuidToBinary(UUID.randomUUID()))
                        .build();

                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(orderEntityForOrderApproveValidation);

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
                OrderApproveForm orderApproveForm = OrderApproveForm.builder()
                    .orderId(UUID.randomUUID().toString())
                    .amount(1000)
                    .paymentKey("tossPaymentsPaymentKey")
                    .build();

                OrderEntityForOrderApproveValidation orderEntityForOrderApproveValidation =
                    OrderEntityForOrderApproveValidation.builder()
                        .orderQuantity(3)
                        .totalPaymentAmount(20000)
                        .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                        .dealProductUuid(TypeConversionUtils.convertUuidToBinary(UUID.randomUUID()))
                        .build();

                given(orderQueryRepository.findOrderEntityForOrderApproveValidation(any())).willReturn(orderEntityForOrderApproveValidation);

                // when + then
                assertThrows(InvalidPaymentAmountException.class, () -> {
                    orderService.approveOrder(orderApproveForm);
                });
            }
        }
    }
}
