package com.hcommerce.heecommerce.order;

import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
class RecipientInfo {
    private final String recipientName;
    private final String recipientPhoneNumber;
    private final String recipientAddress;
    private final String recipientDetailAddress;
    private final String shippingRequest;

    @Builder
    @ConstructorProperties({
        "recipientName",
        "recipientPhoneNumber",
        "recipientAddress",
        "recipientDetailAddress",
        "shippingRequest"}
    )
    public RecipientInfo(
        String recipientName,
        String recipientPhoneNumber,
        String recipientAddress,
        String recipientDetailAddress,
        String shippingRequest
    ) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.recipientAddress = recipientAddress;
        this.recipientDetailAddress = recipientDetailAddress;
        this.shippingRequest = shippingRequest;
    }
}
