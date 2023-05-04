package com.hcommerce.heecommerce.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // TODO : 추후 삭제 필요! 이거 주석처리 후 테스트해야 제대로 테스트 됨
//        HttpSession session = request.getSession();
//        session.setAttribute("isAdmin", true);

        if (!AuthHelper.isLogin(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.setContentType("application/json; charset=UTF-8");

            ErrorResponseDto unauthorizedErrorResponseDto = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.name(), "로그인 후에 이용할 수 있습니다.");
            response.getWriter().append(objectMapper.writeValueAsString(unauthorizedErrorResponseDto));

            return false;
        }

        if(!AuthHelper.isAdmin(request)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());

            response.setContentType("application/json; charset=UTF-8");

            ErrorResponseDto forbiddenErrorResponseDto = new ErrorResponseDto(HttpStatus.FORBIDDEN.name(), "관리자만 이용 가능합니다.");
            response.getWriter().append(objectMapper.writeValueAsString(forbiddenErrorResponseDto));

            return false;
        }

        return true;
    }

    // TODO : 추후 삭제 필요!
    private boolean isRandomAdmin() {
        return System.currentTimeMillis() % 2 == 0;
    }
}
