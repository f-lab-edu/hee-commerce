package com.hcommerce.heecommerce.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderUuid) {
        super(orderUuid + ": 해당 주문을 찾을 수 없습니다.");
    }
}
