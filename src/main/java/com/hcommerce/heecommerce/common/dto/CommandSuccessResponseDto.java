package com.hcommerce.heecommerce.common.dto;

import lombok.Getter;

@Getter
public class CommandSuccessResponseDto {

    private final String message;

    public CommandSuccessResponseDto(String message) {
        this.message = message;
    }
}
