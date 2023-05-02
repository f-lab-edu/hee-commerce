package com.hcommerce.heecommerce.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

}
