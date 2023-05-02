package com.hcommerce.heecommerce.auth.dto.response;

import lombok.Getter;

@Getter
public class UserSignUpResponseDto {

    private Long id;


    public UserSignUpResponseDto(Long id) {
        this.id = id;
    }
}
