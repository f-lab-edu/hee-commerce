package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignUpResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCommandRepository userRepository;

    public UserSignUpResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) throws SQLIntegrityConstraintViolationException {
        Long savedId = userRepository.save(userSignUpRequestDto);
        return new UserSignUpResponseDto(savedId);
    }
}
