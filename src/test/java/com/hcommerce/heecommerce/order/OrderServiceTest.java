package com.hcommerce.heecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

@DisplayName("OrderService")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderCommandRepository orderCommandRepository;

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
}
