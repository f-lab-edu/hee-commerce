package com.hcommerce.heecommerce.order;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderForm {

    private OrdererInfo ordererInfo;
    private RecipientInfo recipientInfo;
    private PaymentInfo paymentInfo;

    @Builder
    public OrderForm(
        OrdererInfo ordererInfo,
        RecipientInfo recipientInfo,
        PaymentInfo paymentInfo
    ) {
        this.ordererInfo = ordererInfo;
        this.recipientInfo = recipientInfo;
        this.paymentInfo = paymentInfo;
    }
}
