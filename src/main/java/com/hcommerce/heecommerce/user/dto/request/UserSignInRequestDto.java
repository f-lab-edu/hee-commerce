package com.hcommerce.heecommerce.user.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserSignInRequestDto {

    @NonNull
    private String loginId;

    @NonNull
    private String password;

    public UserSignInRequestDto(@NonNull String loginId, @NonNull String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
