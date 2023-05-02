package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void save(User user);

}
