package com.hcommerce.heecommerce.order;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * OrderApproveEntity 는 주문 승인 후 주문 데이터를 저장할 때 필요한 클래스이다.
 */
@Getter
public class OrderApproveEntity {

    private final OrderStatus orderStatus = OrderStatus.ORDER_COMPLETED; // 항상 같은 값을 가지므로
    private final int realOrderQuantity;
    private final Instant paymentApprovedAt;

    @Builder
    public OrderApproveEntity(
        int realOrderQuantity,
        Instant paymentApprovedAt
    ) {
        this.realOrderQuantity = realOrderQuantity;
        this.paymentApprovedAt = paymentApprovedAt;
    }
}
