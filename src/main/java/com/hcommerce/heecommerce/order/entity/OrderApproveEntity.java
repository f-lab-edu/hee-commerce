package com.hcommerce.heecommerce.order.entity;

import com.hcommerce.heecommerce.order.enums.OrderStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * OrderApproveEntity 는 주문 승인 후 주문 데이터를 저장할 때 필요한 클래스이다.
 */
@Getter
public class OrderApproveEntity {

    private byte[] orderUuid;
    private final OrderStatus orderStatus = OrderStatus.ORDER_COMPLETED; // 항상 같은 값을 가지므로
    private final int realOrderQuantity;
    private final String paymentKey;
    private final Instant paymentApprovedAt;

    @Builder
    public OrderApproveEntity(
        byte[] orderUuid,
        int realOrderQuantity,
        String paymentKey,
        Instant paymentApprovedAt
    ) {
        this.orderUuid = orderUuid;
        this.realOrderQuantity = realOrderQuantity;
        this.paymentKey = paymentKey;
        this.paymentApprovedAt = paymentApprovedAt;
    }
}
