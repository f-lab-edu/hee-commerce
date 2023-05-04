package com.hcommerce.heecommerce.auth.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponseDto {

    private String loginId;

    private String password;

    public UserSignInResponseDto(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}