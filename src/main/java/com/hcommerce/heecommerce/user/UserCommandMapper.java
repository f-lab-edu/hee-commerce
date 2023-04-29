package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.UserSignUpDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCommandMapper {

    void save(UserSignUpDto user);

}
