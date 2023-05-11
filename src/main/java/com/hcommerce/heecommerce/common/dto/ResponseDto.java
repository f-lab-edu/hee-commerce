package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto {
    private final String code;
    private final String message;

    @Builder
    public ResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
