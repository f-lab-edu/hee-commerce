package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.mapper.UserQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserQueryRepository {

    private final UserQueryMapper userQueryMapper;

    @Autowired
    public UserQueryRepository(UserQueryMapper userQueryMapper) {
        this.userQueryMapper = userQueryMapper;
    }

    public boolean hasUserId(int userId) {
        return userQueryMapper.hasUserId(userId);
    }
}
