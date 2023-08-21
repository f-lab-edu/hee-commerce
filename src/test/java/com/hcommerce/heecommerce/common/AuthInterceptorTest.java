package com.hcommerce.heecommerce.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("AuthInterceptor")
public class AuthInterceptorTest {

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @Mock
    private AuthenticationService authenticationService;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authInterceptor).build();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("when user is login")
    class Context_user_is_login {
        @Test
        @DisplayName("returns 200 OK")
        void It_returns_HTTP_200_OK() throws Exception {
            // given
            given(authenticationService.isAuthenticatedUser(request)).willReturn(true);

            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertTrue(result);

            assertEquals(HttpStatus.OK.value(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("when user is not login")
    class Context_User_is_Not_Login {
        @Test
        @DisplayName("returns 401 error")
        void It_returns_401_Error() throws Exception {
            // given
            given(authenticationService.isAuthenticatedUser(request)).willReturn(false);

            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertFalse(result);

            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        }
    }
}
