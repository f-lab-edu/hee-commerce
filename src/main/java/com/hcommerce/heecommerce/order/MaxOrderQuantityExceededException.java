package com.hcommerce.heecommerce.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MaxOrderQuantityExceededException extends RuntimeException {
    public MaxOrderQuantityExceededException(int maxOrderQuantityPerOrder) {
        super("최대 주문 수량은 " + maxOrderQuantityPerOrder + "개 입니다.");
    }
}
