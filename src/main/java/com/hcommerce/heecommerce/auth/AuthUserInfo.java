package com.hcommerce.heecommerce.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUserInfo {

    private final int userId;

    public AuthUserInfo(int userId) {
        this.userId = userId;
    }
}
