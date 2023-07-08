package com.hcommerce.heecommerce.order;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderEntityForOrderApproveValidation {

    private final byte[] dealProductUuid;
    private final int orderQuantity;
    private final int totalPaymentAmount;
    private final OutOfStockHandlingOption outOfStockHandlingOption;

    @Builder
    public OrderEntityForOrderApproveValidation(
        byte[] dealProductUuid,
        int orderQuantity,
        int totalPaymentAmount,
        OutOfStockHandlingOption outOfStockHandlingOption
    ) {
        this.dealProductUuid = dealProductUuid;
        this.orderQuantity = orderQuantity;
        this.totalPaymentAmount = totalPaymentAmount;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
    }
}
