package com.hcommerce.heecommerce.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrderOverStockException extends RuntimeException {
    public OrderOverStockException() {
        super("재고 수량 부족으로 주문 접수를 할 수 없습니다.");
    }
}
