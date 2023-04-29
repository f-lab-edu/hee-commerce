package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.UserSignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    void signUp(@RequestBody UserSignUpDto userSignUpDto) {
         userService.signUp(userSignUpDto);
    }

}
