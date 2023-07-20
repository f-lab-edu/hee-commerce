package com.hcommerce.heecommerce.inventory.enums;

public enum InventoryEventType {
    ORDER,
    ROLLBACK_BY_POST_VALIDATION_FAILED,
    ROLLBACK_BY_CLOSING_PAYMENT_WINDOW,
    ORDER_CANCELLED,
    PAYMENT_CONFIRM_FAILED
}
