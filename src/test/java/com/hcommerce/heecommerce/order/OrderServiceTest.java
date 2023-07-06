package com.hcommerce.heecommerce.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import com.hcommerce.heecommerce.deal.DealQueryRepository;
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
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .dealProductUuid(UUID.randomUUID())
                    .orderQuantity(2)
                    .paymentMethod(PaymentMethod.CREDIT_CARD)
                    .build();

                UUID expectedOrderUuid = UUID.randomUUID();

                given(orderCommandRepository.saveOrderInAdvance(any())).willReturn(expectedOrderUuid);

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

                given(dealQueryRepository.hasDealProductUuid(NOT_EXIST_DEAL_PRODUCT_UUID)).willReturn(false);

                // when + then
                assertThrows(TimeDealProductNotFoundException.class, () -> {
                    orderService.placeOrderInAdvance(orderForm);
                });
            }
        }
    }
}
