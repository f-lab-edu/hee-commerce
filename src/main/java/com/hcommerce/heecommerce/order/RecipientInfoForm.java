package com.hcommerce.heecommerce.order;

import jakarta.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
class RecipientInfoForm {

    @NotNull(message = "수령인 이름을 입력해주세요.")
    private final String recipientName;

    @NotNull(message = "수령인 전화번호를 입력해주세요.")
    private final String recipientPhoneNumber;

    @NotNull(message = "수령 주소를 입력해주세요.")
    private final String recipientAddress;

    private final String recipientDetailAddress;

    private final String shippingRequest;

    @Builder
    @ConstructorProperties({
        "recipientName",
        "recipientPhoneNumber",
        "recipientAddress",
        "recipientDetailAddress",
        "shippingRequest"
    })
    public RecipientInfoForm(
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
