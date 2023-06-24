package com.hcommerce.heecommerce.order;

import static com.hcommerce.heecommerce.order.OutOfStockHandlingOption.ALL_CANCEL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import com.hcommerce.heecommerce.deal.DealQueryRepository;
import com.hcommerce.heecommerce.deal.TimeDealProductNotFoundException;
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
    private DealQueryRepository dealQueryRepository;

    @InjectMocks
    private OrderService orderService;

    private static final UUID TEMP_NOT_EXIST_UUID = UUID.fromString("8b455042-e709-11ed-93e5-0242ac110002");


    private static final UUID NOT_EXIST_DEAL_PRODUCTUUID = UUID.fromString("8b455042-e709-11ed-93e5-0242ac110001");

    private static final UUID EXIST_DEAL_PRODUCTUUID = UUID.fromString("8b455042-e709-11ed-93e5-0242ac110000");

    private static final int ORDER_QUANTITY_EXCEEDING_MAX_ORDER_QUANTITY = 9999;

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

    @Nested
    @DisplayName("placeOrder")
    class Describe_PlaceOrder {

        @Nested
        @DisplayName("with invalid OrderForm's DealProductUuid")
        class Context_With_Invalid_DealProductUuid {
            @Test
            @DisplayName("throws TimeDealProductNotFoundException")
            void It_throws_TimeDealProductNotFoundException() {
                // given
                given(dealQueryRepository.hasDealProductUuid(NOT_EXIST_DEAL_PRODUCTUUID)).willReturn(false);

                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
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
                    .dealProductUuid(NOT_EXIST_DEAL_PRODUCTUUID)
                    .orderQuantity(1)
                    .paymentType(PaymentType.CREDIT_CARD)
                    .build();

                // then
                assertThrows(TimeDealProductNotFoundException.class, () -> {
                    orderService.placeOrder(orderForm);
                });
            }
        }

        @Nested
        @DisplayName("with invalid OrderForm's OrderQuantity exceeding maxOrderQuantity")
        class Context_With_Invalid_OrderQuantity_Exceeding_MaxOrderQuantity {
            @Test
            @DisplayName("throws MaxOrderQuantityExceededException")
            void It_throws_MaxOrderQuantityExceededException() {
                // given
                given(dealQueryRepository.hasDealProductUuid(EXIST_DEAL_PRODUCTUUID)).willReturn(true);
                given(dealQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(EXIST_DEAL_PRODUCTUUID)).willReturn(10);

                OrderForm orderForm = OrderForm.builder()
                    .userId(1)
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
                    .dealProductUuid(EXIST_DEAL_PRODUCTUUID)
                    .orderQuantity(ORDER_QUANTITY_EXCEEDING_MAX_ORDER_QUANTITY)
                    .paymentType(PaymentType.CREDIT_CARD)
                    .build();

                // then
                assertThrows(MaxOrderQuantityExceededException.class, () -> {
                    orderService.placeOrder(orderForm);
                });
            }
        }
    }
}
