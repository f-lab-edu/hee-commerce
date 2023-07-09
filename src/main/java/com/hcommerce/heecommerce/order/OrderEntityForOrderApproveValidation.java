package com.hcommerce.heecommerce.order;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderEntityForOrderApproveValidation {

    private final byte[] dealProductUuid;
    private final int realOrderQuantity;
    private final int totalPaymentAmount;
    private final OutOfStockHandlingOption outOfStockHandlingOption;

    @Builder
    public OrderEntityForOrderApproveValidation(
        byte[] dealProductUuid,
        int realOrderQuantity,
        int totalPaymentAmount,
        OutOfStockHandlingOption outOfStockHandlingOption
    ) {
        this.dealProductUuid = dealProductUuid;
        this.realOrderQuantity = realOrderQuantity;
        this.totalPaymentAmount = totalPaymentAmount;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
    }
}
