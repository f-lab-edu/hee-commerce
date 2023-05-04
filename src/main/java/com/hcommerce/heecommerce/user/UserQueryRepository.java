package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.auth.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignInResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final UserQueryMapper userQueryMapper;

    public String findByPhoneNumber(String phoneNumber) {
        try {
            return userQueryMapper.findByPhoneNumber(phoneNumber);
        } catch (Exception error) {
            throw error;
        }
    }

    public UserSignInResponseDto findByLoginId(String loginId) {
        try {
            return userQueryMapper.findByLoginId(loginId);
        } catch (Exception error) {
            throw error;
        }
    }

}
