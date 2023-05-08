package com.hcommerce.heecommerce.user.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("user")
@ToString
public class User {
    @Id
    String id;
    String loginId;

    @Builder
    public User(String id, String loginId) {
        this.id = id;
        this.loginId = loginId;
    }
}
