package com.hcommerce.heecommerce.common.dto;

import lombok.Getter;

@Getter
public class MessageResponseDto {

    private final String message;

    public MessageResponseDto(String message) {
        this.message = message;
    }
}
