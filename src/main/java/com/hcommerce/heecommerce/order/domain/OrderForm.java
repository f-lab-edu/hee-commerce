package com.hcommerce.heecommerce.order.domain;

import com.hcommerce.heecommerce.order.dto.OrderFormDto;
import com.hcommerce.heecommerce.order.dto.RecipientInfoForm;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import com.hcommerce.heecommerce.order.exception.MaxOrderQuantityExceededException;
import com.hcommerce.heecommerce.order.exception.OrderOverStockException;
import com.hcommerce.heecommerce.order.exception.TimeDealProductNotFoundException;
import com.hcommerce.heecommerce.user.exception.UserNotFoundException;
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

    public static OrderForm of(OrderFormDto orderFormDto, int userId) {
        return OrderForm.builder()
            .userId(userId)
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
     * validateHasUserId 는 DB에 존재하는 UserId 인지 검사하는 함수이다.
     */
    public void validateHasUserId(boolean hasUserId) {
        if(!hasUserId) {
            throw new UserNotFoundException(this.userId);
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

    /**
     * determineRealOrderQuantity 는 실제 주문 수량을 결정하는 함수 이다.
     *
     * realOrderQuantity 이 필요한 이유는 "부분 주문" 때문이다.
     * 재고량이 0은 아니지만, 사용자가 주문한 수량에 비해 재고량이 없는 경우가 있다.
     * 이때, 재고량만큼만 주문하도록 할 수 있도록 "부문 주문"이 가능한데, 사용자가 주문한 수량과 혼동되지 않도록 실제 주문하는 수량이라는 의미를 내포하기 위해서 필요하다.
     */
    public int determineRealOrderQuantity(int inventory) {
        if(inventory <= 0 || (this.orderQuantity > inventory && this.outOfStockHandlingOption == OutOfStockHandlingOption.ALL_CANCEL)) {
            throw new OrderOverStockException();
        }

        int realOrderQuantity = this.orderQuantity;

        if(this.orderQuantity > inventory && this.outOfStockHandlingOption == OutOfStockHandlingOption.PARTIAL_ORDER) {
            realOrderQuantity = inventory;
        }

        return realOrderQuantity;
    }
}
