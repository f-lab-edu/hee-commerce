package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.SaveUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void save(SaveUserDto userDto) {
        userRepository.save(userDto.of(userDto));
    }
}
