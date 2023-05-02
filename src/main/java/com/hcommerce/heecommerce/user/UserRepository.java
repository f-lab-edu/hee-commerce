package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserMapper userMapper;

    public void save(User user) {
        userMapper.save(user);
    }

}
