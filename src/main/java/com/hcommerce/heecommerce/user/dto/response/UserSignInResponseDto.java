package com.hcommerce.heecommerce.user.dto.response;

import lombok.Getter;

@Getter
public class UserSignInResponseDto {

    private String loginId;

    private String phoneNumber;

    public UserSignInResponseDto(String loginId, String phoneNumber) {
        this.loginId = loginId;
        this.phoneNumber = phoneNumber;
    }
}