package com.hcommerce.heecommerce.order;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderFormSavedInAdvanceEntity {

    private final byte[] uuid;
    private final OrderStatus orderStatus;
    private final int userId;
    private final OutOfStockHandlingOption outOfStockHandlingOption;
    private final byte[] dealProductUuid;
    private final int originalOrderQuantityForPartialOrder;
    private final int realOrderQuantity;
    private final int totalPaymentAmount;
    private final PaymentMethod paymentMethod;
    private final RecipientInfoForm recipientInfoForm;

    @Builder
    public OrderFormSavedInAdvanceEntity(
        byte[] uuid,
        OrderStatus orderStatus,
        int userId,
        OutOfStockHandlingOption outOfStockHandlingOption,
        byte[] dealProductUuid,
        int originalOrderQuantityForPartialOrder,
        int realOrderQuantity,
        int totalPaymentAmount,
        PaymentMethod paymentMethod,
        RecipientInfoForm recipientInfoForm
    ) {
        this.uuid = uuid;
        this.orderStatus = orderStatus;
        this.userId = userId;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
        this.dealProductUuid = dealProductUuid;
        this.originalOrderQuantityForPartialOrder = originalOrderQuantityForPartialOrder;
        this.realOrderQuantity = realOrderQuantity;
        this.totalPaymentAmount = totalPaymentAmount;
        this.paymentMethod = paymentMethod;
        this.recipientInfoForm = recipientInfoForm;
    }
}
