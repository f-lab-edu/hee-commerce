package com.hcommerce.heecommerce.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException() {
        super("결제 금액이 유효하지 않습니다.");
    }
}