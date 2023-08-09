package com.hcommerce.heecommerce.order.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hcommerce.heecommerce.fixture.OrderFixture;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
import com.hcommerce.heecommerce.user.exception.UserNotFoundException;
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
    @DisplayName("validateHasUserId")
    class Describe_ValidateHasUserId {
        @Nested
        @DisplayName("when hasUserId is true")
        class Context_When_HasUserId_Is_True {
            @Test
            @DisplayName("does not throws UserNotFoundException")
            void It_Does_Not_Throws_UserNotFoundException() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                assertDoesNotThrow(() -> {
                    boolean hasUserId = true;

                    orderForm.validateHasUserId(hasUserId);
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

                assertThrows(UserNotFoundException.class, () -> {
                    boolean hasUserId = false;

                    orderForm.validateHasUserId(hasUserId);
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

    @Nested
    @DisplayName("determineRealOrderQuantity")
    class Describe_DetermineRealOrderQuantity {
        @Nested
        @DisplayName("with orderQuantity < inventory")
        class Context_With_OrderQuantity_Is_Less_Than_Inventory {
            @Test
            @DisplayName("returns orderQuantity")
            void It_Returns_OrderQuantity() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                int inventory = OrderFixture.INVENTORY;

                int realOrderQuantity = orderForm.determineRealOrderQuantity(inventory);

                assertEquals(orderForm.getOrderQuantity(), realOrderQuantity);
            }
        }

        @Nested
        @DisplayName("with orderQuantity > inventory and this.outOfStockHandlingOption = OutOfStockHandlingOption.PARTIAL_ORDER")
        class Context_With_orderQuantity_Is_More_Than_Inventory_AND_OutOfStockHandlingOption_Is_PARTIAL_ORDER {
            @Test
            @DisplayName("returns inventory")
            void It_Returns_Inventory() {
                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                    .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_INVENTORY)
                    .outOfStockHandlingOption(OutOfStockHandlingOption.PARTIAL_ORDER)
                    .build();

                int inventory = OrderFixture.INVENTORY;

                int realOrderQuantity = orderForm.determineRealOrderQuantity(inventory);

                assertEquals(inventory, realOrderQuantity);
            }
        }

        @Nested
        @DisplayName("with orderQuantity > inventory and outOfStockHandlingOption = OutOfStockHandlingOption.ALL_CANCEL")
        class Context_With_orderQuantity_Is_More_Than_Inventory_AND_OutOfStockHandlingOption_Is_ALL_CANCEL {
            @Test
            @DisplayName("throws OrderOverStockException")
            void It_throws_MaxOrderQuantityExceededException() {
                OrderForm orderForm = OrderFixture.OrderFormRebuilder()
                    .orderQuantity(OrderFixture.ORDER_QUANTITY_OVER_INVENTORY)
                    .outOfStockHandlingOption(OutOfStockHandlingOption.ALL_CANCEL)
                    .build();

                assertThrows(OrderOverStockException.class, () -> {
                    int inventory = OrderFixture.INVENTORY;

                    orderForm.determineRealOrderQuantity(inventory);
                });
            }
        }

        @Nested
        @DisplayName("with inventory <= 0")
        class   Context_With_inventory_Is_Less_Than_0 {
            @Test
            @DisplayName("throws OrderOverStockException")
            void It_throws_OrderOverStockException() {
                OrderForm orderForm = OrderFixture.ORDER_FORM;

                assertThrows(OrderOverStockException.class, () -> {
                    int inventory = 0;

                    orderForm.determineRealOrderQuantity(inventory);
                });
            }
        }
    }
}
