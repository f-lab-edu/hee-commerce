package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignInResponseDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserQueryMapper {

    String findByPhoneNumber(String phoneNumber);

    UserSignInResponseDto findByLoginIdAndPassword(UserSignInRequestDto userSignInRequestDto);

}
