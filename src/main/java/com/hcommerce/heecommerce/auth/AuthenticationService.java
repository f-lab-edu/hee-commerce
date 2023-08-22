package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.user.UserQueryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * AuthHelper
 * - isAuthenticatedUser
 * - getAuthUserInfo
 *
 * JwtAuthHelper
 *
 * SessionAuthHelper
 *
 */
@Service
public class AuthenticationService {

    private final UserQueryRepository userQueryRepository;

    private final AuthHelper authHelper;

    @Autowired
    public AuthenticationService(
        UserQueryRepository userQueryRepository,
        AuthHelper authHelper
    ) {
        this.userQueryRepository = userQueryRepository;
        this.authHelper = authHelper;
    }

    /**
     * isAuthenticatedUser는 HTTP 요청이 인증된 사용자에 의한 것인지를 판단하는 함수이다.
     */
    public boolean isAuthenticatedUser(HttpServletRequest request) {
        return authHelper.isAuthenticatedUser(request, userQueryRepository);
    }

    /**
     * parseAuthorization는 HTTP Header 의 authorization 를 피상해서 accessToken에 담긴 정보를 리턴하는 함수이다.
     * 각 단계별로 유효성 검사를 할 수 있겠지만, 다른 기능 구현에 집중하기 위해 시간 관계상
     * AuthInterceptor 에서 authorization 의 유효성이 모두 유효하게 판단된 상황을 가정해서 따로 추가하지 않았다.
     */
    public AuthUserInfo getAuthUserInfo(String authInfo) {
        return authHelper.getAuthUserInfo(authInfo);
    }
}
