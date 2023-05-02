package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.auth.dto.request.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandRepository {

    private final UserCommandMapper userCommandMapper;

    public Long save(UserSignUpRequestDto userSignUpRequestDto) {
        try {
            userCommandMapper.save(userSignUpRequestDto);
        }catch (Exception error) {
            throw error;
        }
        return userSignUpRequestDto.getId();
    }

}
