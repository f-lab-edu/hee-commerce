package com.hcommerce.heecommerce.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthHelper {
    // TODO : 임시로 isAdmin에 랜덤값 넣어줌 -> 관리 태희님과 상의하여 user에 담을지 아니면 isAdmin으로 할지 결정할 예정.
    public static boolean isLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session != null && session.getAttribute("isAdmin") != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return session != null && (Boolean) session.getAttribute("isAdmin");
    }
}
