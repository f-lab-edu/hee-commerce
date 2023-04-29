package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandRepository {

    private final UserCommandMapper userCommandMapper;

    public Long save(UserSignUpRequestDto userSignUpRequestDto) {
        userCommandMapper.save(userSignUpRequestDto);
        return userSignUpRequestDto.getId();
    }

}
