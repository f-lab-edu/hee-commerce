package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.auth.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignInResponseDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserQueryMapper {

    String findByPhoneNumber(String phoneNumber);

    UserSignInResponseDto findByLoginId(String loginId);

}
