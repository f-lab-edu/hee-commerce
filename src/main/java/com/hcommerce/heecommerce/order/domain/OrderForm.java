package com.hcommerce.heecommerce.order.domain;

import com.hcommerce.heecommerce.order.dto.OrderFormDto;
import com.hcommerce.heecommerce.order.dto.RecipientInfoForm;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
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

    /**
     * validateHasDealProductUuid 는 DB에 존재하는 dealProductUuid 인지 검사하는 함수이다.
     */
    public void validateHasDealProductUuid(boolean hasDealProductUuid) {
        if(!hasDealProductUuid) {
            throw new TimeDealProductNotFoundException(this.dealProductUuid);
        }
    }

    /**
     * validateOrderQuantityInMaxOrderQuantityPerOrder 는 최대 주문 수량에 맞는지에 대해 검증하는 함수이다.
     */
    public void validateOrderQuantityInMaxOrderQuantityPerOrder(int maxOrderQuantityPerOrder) {
        if(this.orderQuantity > maxOrderQuantityPerOrder) {
            throw new MaxOrderQuantityExceededException(maxOrderQuantityPerOrder);
        }
    }
}
