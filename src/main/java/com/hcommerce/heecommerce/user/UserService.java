package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignInResponseDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignUpResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

    public UserSignUpResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) {


            try {
                String phoneNumber = userQueryRepository.findByPhoneNumber(userSignUpRequestDto.getPhoneNumber());
                if (phoneNumber != null) {
                    throw new Exception("이미 가입된 회원이 있습니다.");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        Long savedId = userCommandRepository.save(userSignUpRequestDto);
        return new UserSignUpResponseDto(savedId);
    }

    public UserSignInResponseDto signIn(UserSignInRequestDto userSignInRequestDto) {
        UserSignInResponseDto foundUser = userQueryRepository.findByLoginIdAndPassword(userSignInRequestDto);


        if (foundUser == null) {
            throw new RuntimeException("회원가입 하지 않은 유저입니다.");
        }

        return foundUser;
    }
}
