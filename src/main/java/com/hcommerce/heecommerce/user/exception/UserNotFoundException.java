package com.hcommerce.heecommerce.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int userId) {
        super(userId + ": 해당 유저를 찾을 수 없습니다.");
    }
}
