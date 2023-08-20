package com.hcommerce.heecommerce.auth;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super(token +" : 유효하지 않은 토큰 입니다.");
    }
}
