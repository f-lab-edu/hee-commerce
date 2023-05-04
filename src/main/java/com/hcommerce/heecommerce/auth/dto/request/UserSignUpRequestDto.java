package com.hcommerce.heecommerce.auth.dto.request;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserSignUpRequestDto {

    private Long id;

    @NonNull
    private String loginId;

    @NonNull
    private String phoneNumber;

    @NonNull
    private String password;

    @NonNull
    private String mainAddress;

    @Nullable
    private String detailAddress;

    public UserSignUpRequestDto() { }

    public UserSignUpRequestDto(@NonNull String loginId, @NonNull String phoneNumber, @NonNull String password, @NonNull String mainAddress) {
        this.loginId = loginId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.mainAddress = mainAddress;
    }

    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }
}
