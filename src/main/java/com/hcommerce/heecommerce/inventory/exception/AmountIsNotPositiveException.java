package com.hcommerce.heecommerce.inventory.exception;

public class AmountIsNotPositiveException extends RuntimeException {
    public AmountIsNotPositiveException() {
        super("수량이 양수인지 확인해주세요");
    }
}
