package com.hcommerce.heecommerce.order.entity;

import com.hcommerce.heecommerce.order.enums.OrderStatus;
import com.hcommerce.heecommerce.order.enums.OutOfStockHandlingOption;
import com.hcommerce.heecommerce.order.enums.PaymentMethod;
import com.hcommerce.heecommerce.order.dto.RecipientInfoForm;
import lombok.Builder;
import lombok.Getter;

/**
 * OrderFormSavedInAdvanceEntity 는 주문 내역 사전 저장에 필요한 클래스이다.
 *
 * originalOrderQuantityForPartialOrder 를 Integer 로 데이터 타입을 정한 이유
 * Q1) int 대신 Integer 사용한 이유
 * 쿼리를 단순화 하고 싶었기 떄문이다.
 * order 테이블에서 originalOrderQuantityForPartialOrder 는 부분 주문이 아닌 전체 주문인 경우에 Null 값을 갖는다.
 * 그런데, int 의 경우, null 값을 허용하지 않아, 동적 쿼리를 필요로 하는데, 쿼리 작성이 복잡해지는 경향이 있다.
 * 이에 null 값을 허용하는 Integer 로 데이터 타입을 바꿔 동적 쿼리를 제거할 수 있었다.
 *
 * Q2) Optional<Integer> 도 가능할 것 같은데, Integer 사용한 이유 : https://github.com/f-lab-edu/hee-commerce/issues/143 참고
 */
@Getter
public class OrderFormSavedInAdvanceEntity {

    private final byte[] uuid;
    private final OrderStatus orderStatus = OrderStatus.PAYMENT_PENDING; // 고정값이므로,
    private final int userId;
    private final OutOfStockHandlingOption outOfStockHandlingOption;
    private final byte[] dealProductUuid;
    private final Integer originalOrderQuantityForPartialOrder;
    private final int realOrderQuantity;
    private final int totalPaymentAmount;
    private final PaymentMethod paymentMethod;
    private final RecipientInfoForm recipientInfoForm;

    @Builder
    public OrderFormSavedInAdvanceEntity(
        byte[] uuid,
        int userId,
        OutOfStockHandlingOption outOfStockHandlingOption,
        byte[] dealProductUuid,
        Integer originalOrderQuantityForPartialOrder,
        int realOrderQuantity,
        int totalPaymentAmount,
        PaymentMethod paymentMethod,
        RecipientInfoForm recipientInfoForm
    ) {
        this.uuid = uuid;
        this.userId = userId;
        this.outOfStockHandlingOption = outOfStockHandlingOption;
        this.dealProductUuid = dealProductUuid;
        this.originalOrderQuantityForPartialOrder = originalOrderQuantityForPartialOrder;
        this.realOrderQuantity = realOrderQuantity;
        this.totalPaymentAmount = totalPaymentAmount;
        this.paymentMethod = paymentMethod;
        this.recipientInfoForm = recipientInfoForm;
    }
}
