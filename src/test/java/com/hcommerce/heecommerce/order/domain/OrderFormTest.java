package com.hcommerce.heecommerce.order.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hcommerce.heecommerce.fixture.OrderFixture;
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
}