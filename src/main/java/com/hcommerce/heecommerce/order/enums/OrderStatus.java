package com.hcommerce.heecommerce.order.enums;

/**
 * OrderStatus 는 주문 상태를 관리하는 Enum 클래스이다.
 * TODO : 일단 생각나는 상태들만 추가하였고, 추후에 확장성을 고려해서 더 다양한 상태를 담을 수 있고, 상태 간의 전환에 대한 예외처리도 생각해볼 예정
 */
public enum OrderStatus {
    PAYMENT_PENDING,
    ORDER_COMPLETED,
    ORDER_CANCELLED,
    DELIVERY_PENDING,
    IN_DELIVERY,
    DELIVERY_COMPLETED
}
