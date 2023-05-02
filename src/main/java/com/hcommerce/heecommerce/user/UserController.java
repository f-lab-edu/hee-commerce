package com.hcommerce.heecommerce.user;

import com.hcommerce.heecommerce.user.constant.SessionConstant;
import com.hcommerce.heecommerce.user.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.user.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignInResponseDto;
import com.hcommerce.heecommerce.user.dto.response.UserSignUpResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    UserSignUpResponseDto signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        return userService.signUp(userSignUpRequestDto);
    }

    @PostMapping("/sign-in")
    UserSignInResponseDto signIn(@RequestBody UserSignInRequestDto userSignInRequestDto, HttpServletRequest httpServletRequest) {

        UserSignInResponseDto userSignInResponseDto = userService.signIn(userSignInRequestDto);

        HttpSession session = httpServletRequest.getSession();

        session.setAttribute(SessionConstant.LOGIN_USER, userSignInResponseDto);

        return userSignInResponseDto;
    }

    @PostMapping("/logOut")
    void logOut(HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

}
