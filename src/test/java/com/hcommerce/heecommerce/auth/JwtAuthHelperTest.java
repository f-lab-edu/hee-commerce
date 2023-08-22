package com.hcommerce.heecommerce.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.common.utils.JwtUtils;
import com.hcommerce.heecommerce.fixture.AuthFixture;
import com.hcommerce.heecommerce.fixture.JwtFixture;
import com.hcommerce.heecommerce.fixture.UserFixture;
import com.hcommerce.heecommerce.user.UserQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("JwtAuthHelper")
class JwtAuthHelperTest {

    @Mock
    private JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String secret;

    @InjectMocks
    private JwtAuthHelper jwtAuthHelper;

    @Mock
    private UserQueryRepository userQueryRepository;

    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
    }

    @Nested
    @DisplayName("isAuthenticatedUser")
    class Describe_IsAuthenticatedUser {
        @Nested
        @DisplayName("with valid authorization")
        class Context_With_Valid_Authorization {
            @Test
            @DisplayName("returns true")
            void It_returns_true() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION);

                given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

                given(userQueryRepository.hasUserId(anyInt())).willReturn(true);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("when authorization is empty")
        class Context_When_Authorization_Is_Empty {
            @Test
            @DisplayName("returns false")
            void It_returns_false() {
                // given
                request.addHeader("Authorization", "");

                given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

                given(userQueryRepository.hasUserId(anyInt())).willReturn(true);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertFalse(result);
            }
        }

        @Nested
        @DisplayName("with invalid authType")
        class Context_With_Invalid_AuthType {
            @Test
            @DisplayName("returns false")
            void It_returns_false() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION_WITH_INVALID_AUTH_TYPE);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertFalse(result);
            }
        }

        // accessToken
        @Nested
        @DisplayName("with invalid accessToken")
        class Context_With_Invalid_AccessToken {
            @Test
            @DisplayName("returns false")
            void It_returns_false() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION_WITHOUT_TOKEN);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertFalse(result);
            }
        }

        @Nested
        @DisplayName("with invalid authUserInfo")
        class Context_With_Invalid_AuthUserInfo {
            @Test
            @DisplayName("returns false")
            void It_returns_false() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION_WITHOUT_TOKEN_PAYLOAD);

                given(jwtUtils.decode(any())).willReturn(null);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertFalse(result);
            }
        }
        // authUserInfo

        @Nested
        @DisplayName("with invalid userId")
        class Context_With_Invalid_UserId {
            @Test
            @DisplayName("returns true")
            void It_returns_true() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION);

                given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

                given(userQueryRepository.hasUserId(anyInt())).willReturn(false);

                // when
                boolean result = jwtAuthHelper.isAuthenticatedUser(request, userQueryRepository);

                // then
                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("getAuthUserInfo")
    class Describe_GetAuthUserInfo {
        @Test
        @DisplayName("returns tokenPayload with `userId`")
        void It_returns_TokenPayload() {
            given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

            AuthUserInfo authUserInfo = jwtAuthHelper.getAuthUserInfo(AuthFixture.AUTHORIZATION);

            assertEquals(authUserInfo.getUserId(), UserFixture.ID);
        }
    }
}
