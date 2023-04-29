package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.UserSignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandRepository {

    private final UserCommandMapper userCommandMapper;

    public void save(UserSignUpDto user) {
        userCommandMapper.save(user);
    }

}
