package com.hcommerce.heecommerce.order.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TimeDealProductNotFoundException extends RuntimeException {
    public TimeDealProductNotFoundException(UUID orderUuid) {
        super(orderUuid + " : 해당 타임 딜 상품을 찾을 수 없습니다.");
    }
}