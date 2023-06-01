package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {
    private final String code;
    private final String message;
    private final T data;

    @Builder
    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
