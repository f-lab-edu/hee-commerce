package com.hcommerce.heecommerce.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    private final JwtAuthHelper jwtAuthHelper;

    @Autowired
    public AuthConfig(JwtAuthHelper jwtAuthHelper) {
        this.jwtAuthHelper = jwtAuthHelper;
    }

    @Bean
    public AuthHelper authHelper() {
        return jwtAuthHelper; // TODO : 추후에 세션으로 관리하는 걸로 바뀌면 이곳 수정해줘야 함.
    }
}
