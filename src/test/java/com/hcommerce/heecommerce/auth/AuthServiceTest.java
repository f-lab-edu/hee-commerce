package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.auth.dto.request.UserSignUpRequestDto;
import com.hcommerce.heecommerce.auth.dto.response.UserSignUpResponseDto;
import com.hcommerce.heecommerce.user.UserCommandRepository;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import com.hcommerce.heecommerce.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {


    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Sign-Up")
    class SignUpMethods {
        @Test
        void Must_Return_Auto_Generated_Id_After_SignUp() {
            // given
            String loginId = "loginId";
            String phoneNumber = "01000000000";
            String password = "password";
            String mainAddress = "mainAddress";

            UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(loginId, phoneNumber, password, mainAddress);

            given(userCommandRepository.save(userSignUpRequestDto)).willReturn(1L);

            // when
            UserSignUpResponseDto userSignUpResponseDto = authService.signUp(userSignUpRequestDto);

            // then
            assertEquals(userSignUpResponseDto.getId(), 1L);
        }

        @Test
        void Phone_Number_Have_To_Be_Unique() {
            // given
            String loginId = "loginId";
            String phoneNumber = "01000000000";
            String password = "password";
            String mainAddress = "mainAddress";

            UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(loginId, phoneNumber, password, mainAddress);

            given(userQueryRepository.findByPhoneNumber(userSignUpRequestDto.getPhoneNumber())).willReturn(phoneNumber).willThrow();

            // when
            // then
            assertThrows(RuntimeException.class, () -> {
                authService.signUp(userSignUpRequestDto);
            });
        }
    }

    @Nested
    @DisplayName("Sign-In")
    class SignInMethod {
        @Test
        void Must_Return_Auto_Generated_Id_After_SignUp() {
            // given
            String loginId = "loginId";
            String phoneNumber = "01000000000";
            String password = "password";
            String mainAddress = "mainAddress";

            UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(loginId, phoneNumber, password, mainAddress);

            given(userCommandRepository.save(userSignUpRequestDto)).willReturn(1L);

            // when
            UserSignUpResponseDto userSignUpResponseDto = authService.signUp(userSignUpRequestDto);

            // then
            assertEquals(userSignUpResponseDto.getId(), 1L);
        }

        @Test
        void Phone_Number_Have_To_Be_Unique() {
            // given
            String loginId = "loginId";
            String phoneNumber = "01000000000";
            String password = "password";
            String mainAddress = "mainAddress";

            UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(loginId, phoneNumber, password, mainAddress);

            given(userQueryRepository.findByPhoneNumber(userSignUpRequestDto.getPhoneNumber())).willReturn(phoneNumber).willThrow();

            // when
            // then
            assertThrows(RuntimeException.class, () -> {
                authService.signUp(userSignUpRequestDto);
            });
        }
    }
}