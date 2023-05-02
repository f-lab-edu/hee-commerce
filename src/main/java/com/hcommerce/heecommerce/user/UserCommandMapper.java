package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.auth.dto.request.UserSignUpRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCommandMapper {

    Long save(UserSignUpRequestDto user);

}
