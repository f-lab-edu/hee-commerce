package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.auth.constant.SessionConstant;
import com.hcommerce.heecommerce.auth.dto.request.UserSignInRequestDto;
import com.hcommerce.heecommerce.auth.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignInResponseDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignUpResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/user/sign-up")
    UserSignUpResponseDto signUp(@RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        return authService.signUp(userSignUpRequestDto);
    }

    @PostMapping("/user/sign-in")
    UserSignInResponseDto signIn(@RequestBody UserSignInRequestDto userSignInRequestDto, HttpServletRequest httpServletRequest) {

        UserSignInResponseDto userSignInResponseDto = authService.signIn(userSignInRequestDto);

        HttpSession session = httpServletRequest.getSession();

        session.setAttribute(SessionConstant.LOGIN_USER, userSignInResponseDto);

        return userSignInResponseDto;
    }

    @PostMapping("/user/logOut")
    void logOut(HttpServletRequest httpServletRequest) {

        HttpSession session = httpServletRequest.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

}
