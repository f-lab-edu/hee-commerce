package com.hcommerce.heecommerce.config;

import com.hcommerce.heecommerce.user.UserMapper;
import com.hcommerce.heecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MyBatisConfig {

    private final UserMapper userMapper;

    @Bean
    public UserRepository itemRepository() {
        return new UserRepository(userMapper);
    }

}
