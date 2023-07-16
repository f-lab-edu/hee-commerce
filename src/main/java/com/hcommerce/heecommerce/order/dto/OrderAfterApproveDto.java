package com.hcommerce.heecommerce.order.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

/**
 * OrderAfterApproveDto 는 주문 승인 후 주문 데이터를 OrderService -> OrderCommandRepository 전송할 때 필요한 클래스이다.
 */
@Getter
public class OrderAfterApproveDto {

    private byte[] orderUuid;
    private final int realOrderQuantity;
    private final String paymentKey;
    private final Instant paymentApprovedAt;

    @Builder
    public OrderAfterApproveDto(
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
