package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.common.utils.JwtUtils;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 현재 JWT 토큰 사용하는 상황을 가정했는데, 세션 저장소 사용도 고려 중인 상황을 대비하여 AbstractAuthHepler 추상 클래스로 하고, SessionAuthHelper 또는 JwtAuthHelper
 * 인터페이스가 아닌 abstract 로 한 이유는 아직 결정되서 완성된 것이 아니라는 의도를 드러내고 싶어서?
 */
@Service
public class AuthenticationService {

    private final String AUTH_TYPE = "Bearer";

    private final JwtUtils jwtUtils;

    private final UserQueryRepository userQueryRepository;

    @Autowired
    public AuthenticationService(JwtUtils jwtUtils, UserQueryRepository userQueryRepository) {
        this.jwtUtils = jwtUtils;
        this.userQueryRepository = userQueryRepository;
    }

    // TODO : 테스트용으로 일단 간단하게 구현함.
    public String login(int userId) {
        return jwtUtils.encode(userId);
    }

    /**
     * isAuthenticatedUser는 HTTP 요청이 인증된 사용자에 의한 것인지를 판단하는 함수이다.
     */
    public boolean isAuthenticatedUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if(authorization == null || authorization.isBlank()) {
            return false;
        }

        if(!isValidAuthType(authorization)) {
            return false;
        }

        String accessToken = extractAccessToken(authorization);

        if(accessToken == null || accessToken.isBlank()) {
            return false;
        }

        AuthUserInfo authUserInfo = parseAccessToken(accessToken);

        if(authUserInfo == null) {
            return false;
        }

        boolean hasUserId = userQueryRepository.hasUserId(authUserInfo.getUserId());

        if(!hasUserId) {
            return false;
        }

        return true;
    }

    /**
     * parseAuthorization는 HTTP Header 의 authorization 를 피상해서 accessToken에 담긴 정보를 리턴하는 함수이다.
     * 각 단계별로 유효성 검사를 할 수 있겠지만, 다른 기능 구현에 집중하기 위해 시간 관계상
     * AuthInterceptor 에서 authorization 의 유효성이 모두 유효하게 판단된 상황을 가정해서 따로 추가하지 않았다.
     */
    public AuthUserInfo parseAuthorization(String authorization) {
        String accessToken = extractAccessToken(authorization);

        AuthUserInfo authUserInfo = parseAccessToken(accessToken);

        return authUserInfo;
    }

    /**
     * isValidAuthType 는 HTTP Header 의 authorization 의 인증 유형이 유효한 인증 유형인지를 판단하는 함수이다.
     */
    private boolean isValidAuthType(String authorization) {
        return authorization.startsWith(AUTH_TYPE);
    }

    /**
     * isValidAuthType 는 HTTP Header 의 authorization로부터 accessToken을 추춣하는 함수이다.
     */
    private String extractAccessToken(String authorization) {
        String[] authorizationUnit = authorization.split(AUTH_TYPE+" ");

        if(authorizationUnit.length < 2) {
            return null;
        }

        String accessToken = authorizationUnit[1];

        if(accessToken.isBlank()) {
            return null;
        }

        return accessToken;
    }

    /**
     * parseAccessToken 는 accessToken에 파싱하여 저장된 사용자 인증 정보를 리턴하는 함수이다.
     */
    private AuthUserInfo parseAccessToken(String accessToken) {
        Claims claims = jwtUtils.decode(accessToken);

        if(claims == null) {
            return null;
        }

        int userId = claims.get("userId", Integer.class);

        return new AuthUserInfo(userId);
    }
}
