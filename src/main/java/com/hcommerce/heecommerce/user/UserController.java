package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.dto.SaveUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/save")
    void save(@RequestBody SaveUserDto userDto) {
         userService.save(userDto);
    }

}
