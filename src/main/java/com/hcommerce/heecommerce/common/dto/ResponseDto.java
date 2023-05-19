package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto {
    private final String code;
    private final String message;
    private final Object data;

    @Builder
    public ResponseDto(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
