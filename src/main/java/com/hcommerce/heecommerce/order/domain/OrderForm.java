package com.hcommerce.heecommerce.order.domain;

import com.hcommerce.heecommerce.order.dto.OrderFormDto;
import com.hcommerce.heecommerce.order.dto.RecipientInfoForm;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderForm {

    private final UUID orderUuid;

    private final int userId;

    private final RecipientInfoForm recipientInfoForm;

    private final OutOfStockHandlingOption outOfStockHandlingOption;

    private final UUID dealProductUuid;

    private final int orderQuantity;

    private final PaymentMethod paymentMethod;

    @Builder
    public OrderForm(
        UUID orderUuid,
        int userId,
        RecipientInfoForm recipientInfoForm,
        OutOfStockHandlingOption outOfStockHandlingOption,
        UUID dealProductUuid,
        int orderQuantity,
        PaymentMethod paymentMethod
    ) {
        this.userId = userId;
        this.orderUuid = orderUuid;
        this.recipientInfoForm = recipientInfoForm;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
        this.dealProductUuid = dealProductUuid;
        this.orderQuantity = orderQuantity;
        this.paymentMethod = paymentMethod;
    }

    public static OrderForm from(OrderFormDto orderFormDto) {
        return OrderForm.builder()
            .userId(orderFormDto.getUserId())
            .orderUuid(orderFormDto.getOrderUuid())
            .recipientInfoForm(orderFormDto.getRecipientInfoForm())
            .outOfStockHandlingOption(orderFormDto.getOutOfStockHandlingOption())
            .dealProductUuid(orderFormDto.getDealProductUuid())
            .orderQuantity(orderFormDto.getOrderQuantity())
            .paymentMethod(orderFormDto.getPaymentMethod())
            .build();
    }
}
