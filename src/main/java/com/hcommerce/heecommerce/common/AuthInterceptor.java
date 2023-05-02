package com.hcommerce.heecommerce.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {


        // TODO : 추후 삭제 필요! 이거 주석처리 후 테스트해야 제대로 테스트 됨
//        HttpSession session = request.getSession();
//        session.setAttribute("isAdmin", isRandomAdmin();

        if (!AuthHelper.isLogin(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if(!AuthHelper.isAdmin(request)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        return true;
    }

    // TODO : 추후 삭제 필요!
    private boolean isRandomAdmin() {
        return System.currentTimeMillis() % 2 == 0;
    }
}
