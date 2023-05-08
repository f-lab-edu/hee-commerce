package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.redis.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserRedisRepository userRedisRepository;

}
