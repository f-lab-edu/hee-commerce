package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.UserSignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCommandRepository userRepository;

    public void signUp(UserSignUpDto userSignUpDto) {
        userRepository.save(userSignUpDto);
    }
}
