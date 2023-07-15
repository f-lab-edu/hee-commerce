package com.hcommerce.heecommerce.payment;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class TosspaymentsException extends RuntimeException {

    private final HttpStatusCode tossHttpStatusCode;
    private final String code;
    private final String message;

    @Builder
    public TosspaymentsException(HttpStatusCode tossHttpStatusCode, String code, String message) {
        this.tossHttpStatusCode = tossHttpStatusCode;
        this.code = code;
        this.message = message;
    }
}
