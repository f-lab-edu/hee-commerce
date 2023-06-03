package com.hcommerce.heecommerce.order;

import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderForm {

    private OrdererInfo ordererInfo;
    private RecipientInfo recipientInfo;
    private PaymentInfo paymentInfo;

    @Builder
    @ConstructorProperties({
        "ordererInfo",
        "recipientInfo",
        "paymentInfo"}
    )
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
