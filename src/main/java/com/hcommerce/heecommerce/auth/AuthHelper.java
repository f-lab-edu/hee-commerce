package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.user.UserQueryRepository;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthHelper {

    boolean isAuthenticatedUser(HttpServletRequest request, UserQueryRepository userQueryRepository);

    AuthUserInfo getAuthUserInfo(String authInfo);
}
