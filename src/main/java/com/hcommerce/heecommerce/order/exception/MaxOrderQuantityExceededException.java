package com.hcommerce.heecommerce.order.exception;


public class MaxOrderQuantityExceededException extends RuntimeException {

    public MaxOrderQuantityExceededException(int maxOrderQuantityPerOrder) {
        super("최대 주문수량은 "+ maxOrderQuantityPerOrder + "개 입니다.");
    }
}
