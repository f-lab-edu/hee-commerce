package com.hcommerce.heecommerce.order;

import java.beans.ConstructorProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
class OrdererInfo {
    private final int userId;
    private final String ordererName;
    private final String ordererPhoneNumber;

    @Builder
    @ConstructorProperties({"userId", "ordererName", "ordererPhoneNumber"})
    public OrdererInfo(int userId, String ordererName, String ordererPhoneNumber) {
        this.userId = userId;
        this.ordererName = ordererName;
        this.ordererPhoneNumber = ordererPhoneNumber;
    }
}
