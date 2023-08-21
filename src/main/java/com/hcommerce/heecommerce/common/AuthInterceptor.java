package com.hcommerce.heecommerce.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcommerce.heecommerce.auth.AuthenticationService;
import com.hcommerce.heecommerce.common.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AuthInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!authenticationService.isAuthenticatedUser(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.setContentType("application/json; charset=UTF-8");

            response.getWriter().append(
                objectMapper.writeValueAsString(ResponseDto.builder()
                    .code(HttpStatus.UNAUTHORIZED.name())
                    .message("로그인 후에 이용할 수 있습니다.")
                    .build())
            );

            return false;
        }

        return true;
    }
}
