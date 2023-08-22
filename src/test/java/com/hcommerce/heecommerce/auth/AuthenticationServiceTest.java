package com.hcommerce.heecommerce.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.fixture.AuthFixture;
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

@DisplayName("AuthenticationService")
class AuthenticationServiceTest {

    @Value("${jwt.secret}")
    private String secret;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private AuthHelper authHelper;

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

                given(authHelper.isAuthenticatedUser(any(), any())).willReturn(true);

                // when
                boolean result = authenticationService.isAuthenticatedUser(request);

                // then
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("with invalid authorization")
        class Context_With_Invalid_Authorization {
            @Test
            @DisplayName("returns false")
            void It_returns_false() {
                // given
                request.addHeader("Authorization", AuthFixture.AUTHORIZATION);

                given(authHelper.isAuthenticatedUser(any(), any())).willReturn(false);

                // when
                boolean result = authenticationService.isAuthenticatedUser(request);

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
            given(authHelper.getAuthUserInfo(any())).willReturn(new AuthUserInfo(1));

            AuthUserInfo authUserInfo = authenticationService.getAuthUserInfo(AuthFixture.AUTHORIZATION);

            assertEquals(authUserInfo.getUserId(), UserFixture.ID);
        }
    }
}
