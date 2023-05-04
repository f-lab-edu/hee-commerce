package com.hcommerce.heecommerce.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private final String code;
    private final String message;
}
