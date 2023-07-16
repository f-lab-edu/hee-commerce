package com.hcommerce.heecommerce.order.dto;

import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderApproveForm {

    @NotNull
    private final String paymentKey; // 토스 키

    @NotNull
    private final String orderId; // 토스 주문 식별자

    @NotNull
    private final int amount; // 총 결제 금액

    @Builder
    @ConstructorProperties({
        "paymentKey",
        "orderId",
        "amount"
    })
    public OrderApproveForm(String paymentKey, String orderId, int amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
