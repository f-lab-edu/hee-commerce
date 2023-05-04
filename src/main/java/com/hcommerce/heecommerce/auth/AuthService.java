package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.auth.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.auth.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignInResponseDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignUpResponseDto;
import com.hcommerce.heecommerce.user.UserCommandRepository;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

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

        userSignUpRequestDto.setHashedPassword(CryptoHelper.hashPassword(userSignUpRequestDto.getPassword()));

        Long savedId = userCommandRepository.save(userSignUpRequestDto);
        return new UserSignUpResponseDto(savedId);
    }

    public String signIn(UserSignInRequestDto userSignInRequestDto) {

        UserSignInResponseDto foundUser = userQueryRepository.findByLoginId(userSignInRequestDto.getLoginId());

        if (foundUser == null) {
            throw new RuntimeException("회원가입 하지 않은 유저입니다.");
        }

        if (!CryptoHelper.checkPassword(userSignInRequestDto.getPassword(), foundUser.getPassword())) {
            throw new RuntimeException("비밀번호가 다르다.");
        }

        return foundUser.getLoginId();
    }
}
