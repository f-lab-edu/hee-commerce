package com.hcommerce.heecommerce.order.dto;

import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderForOrderApproveValidationDto {

    private final byte[] dealProductUuid;
    private final int realOrderQuantity;
    private final int totalPaymentAmount;
    private final OutOfStockHandlingOption outOfStockHandlingOption;

    @Builder
    public OrderForOrderApproveValidationDto(
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
