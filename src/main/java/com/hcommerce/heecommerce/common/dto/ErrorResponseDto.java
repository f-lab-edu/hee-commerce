package com.hcommerce.heecommerce.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final String code;
    private final String message;

    @Builder
    public ErrorResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
