package com.hcommerce.heecommerce.user.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("user")
public class User {
    @Id
    String id;
    String loginId;

    @Builder
    public User(String userId, String loginId) {
        this.id = userId;
        this.loginId = loginId;
    }
}
