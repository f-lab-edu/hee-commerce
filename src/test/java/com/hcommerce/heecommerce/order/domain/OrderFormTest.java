package com.hcommerce.heecommerce.order.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hcommerce.heecommerce.fixture.OrderFixture;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OrderForm")
class OrderFormTest {

    @Nested
    @DisplayName("validateHasDealProductUuid")
    class Describe_ValidateHasDealProductUuid {
        @Nested
        @DisplayName("when hasDealProductUuid is true")
        class Context_When_HasDealProductUuid_Is_True {
            @Test
            @DisplayName("does not throws TimeDealProductNotFoundException")
            void It_Does_Not_Throws_TimeDealProductNotFoundException() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                assertDoesNotThrow(() -> {
                    boolean hasDealProductUuid = true;

                    orderForm.validateHasDealProductUuid(hasDealProductUuid);
                });
            }
        }

        @Nested
        @DisplayName("when hasDealProductUuid is false")
        class Context_When_HasDealProductUuid_Is_False {
            @Test
            @DisplayName("throws TimeDealProductNotFoundException")
            void It_throws_TimeDealProductNotFoundException() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                assertThrows(TimeDealProductNotFoundException.class, () -> {
                    boolean hasDealProductUuid = false;

                    orderForm.validateHasDealProductUuid(hasDealProductUuid);
                });
            }
        }
    }

    @Nested
    @DisplayName("validateOrderQuantityInMaxOrderQuantityPerOrder")
    class Describe_ValidateOrderQuantityInMaxOrderQuantityPerOrder {
        @Nested
        @DisplayName("with orderQuantity < maxOrderQuantityPerOrder")
        class Context_With_orderQuantity_Is_Less_Than_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("does not throws MaxOrderQuantityExceededException")
            void It_Does_Not_Throws_MaxOrderQuantityExceededException() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                assertDoesNotThrow(() -> {
                    int maxOrderQuantityPerOrder = OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER;

                    orderForm.validateOrderQuantityInMaxOrderQuantityPerOrder(maxOrderQuantityPerOrder);
                });
            }
        }

        @Nested
        @DisplayName("with orderQuantity > maxOrderQuantityPerOrder")
        class Context_With_orderQuantity_Is_More_Than_MaxOrderQuantityPerOrder {
            @Test
            @DisplayName("throws MaxOrderQuantityExceededException")
            void It_throws_MaxOrderQuantityExceededException() {
                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                    .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_MAX_ORDER_QUANTITY_PER_ORDER)
                    .build();

                assertThrows(MaxOrderQuantityExceededException.class, () -> {
                    int maxOrderQuantityPerOrder = OrderFixture.MAX_ORDER_QUANTITY_PER_ORDER;

                    orderForm.validateOrderQuantityInMaxOrderQuantityPerOrder(maxOrderQuantityPerOrder);
                });
            }
        }
    }
}