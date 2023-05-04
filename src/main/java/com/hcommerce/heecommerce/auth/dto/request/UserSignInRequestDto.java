package com.hcommerce.heecommerce.auth.dto.request;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserSignInRequestDto {

    @NonNull
    private String loginId;

    @NonNull
    private String password;

    public UserSignInRequestDto() {
    }

    public UserSignInRequestDto(@NonNull String loginId, @NonNull String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }
}
