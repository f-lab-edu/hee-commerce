package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.SQLIntegrityConstraintViolationException;

@Repository
@RequiredArgsConstructor
public class UserCommandRepository {

    private final UserCommandMapper userCommandMapper;

    public Long save(UserSignUpRequestDto userSignUpRequestDto) throws SQLIntegrityConstraintViolationException {
        userCommandMapper.save(userSignUpRequestDto);
        return userSignUpRequestDto.getId();
    }

}
