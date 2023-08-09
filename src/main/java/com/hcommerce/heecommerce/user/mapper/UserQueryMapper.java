package com.hcommerce.heecommerce.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserQueryMapper {

    boolean hasUserId(@Param("userId") int userId);
}
