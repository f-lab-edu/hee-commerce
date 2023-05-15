package com.hcommerce.heecommerce.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthInterceptor")
class AuthInterceptorTest {

    private AuthInterceptor authInterceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authInterceptor = new AuthInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
    }

    @Nested
    @DisplayName("when login user is admin")
    class Context_LoginUser_is_Admin {
        @Test
        @DisplayName("returns 200 OK")
        void it_returns_HTTP_200_OK() throws Exception {
            // given
            session.setAttribute("isAdmin", true);
            request.setSession(session);

            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertTrue(result);
            assertEquals(HttpStatus.OK.value(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("when login user is not admin")
    class Context_LoginUser_is_Not_Admin {
        @Test
        @DisplayName("returns 403 error")
        void it_returns_403_Error() throws Exception {
            // given
            session.setAttribute("isAdmin", false);
            request.setSession(session);

            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertFalse(result);
            assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("when user is not login")
    class Context_User_is_Not_Login {
        @Test
        @DisplayName("returns 401 error")
        void it_returns_401_Error() throws Exception {
            // given
            request.setSession(null);

            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertFalse(result);
            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        }
    }
}
