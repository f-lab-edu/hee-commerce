package com.hcommerce.heecommerce.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();

        // TODO : 추후 삭제 필요!
        session.setAttribute("isAdmin", isRandomAdmin());

        // TODO : 임시로 isAdmin에 랜덤값 넣어줌 -> 관리 태희님과 상의하여 user에 담을지 아니면 isAdmin으로 할지 결정할 예정.
        if (session == null || session.getAttribute("isAdmin") == null || !(boolean) session.getAttribute("isAdmin")) {
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
