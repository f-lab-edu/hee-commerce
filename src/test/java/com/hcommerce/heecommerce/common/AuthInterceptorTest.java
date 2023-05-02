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
    @DisplayName("로그인한 사용자가 관리자인 경우")
    class Context_LoginUser_is_Admin {
        @BeforeEach
        void setUp() {
            session.setAttribute("isAdmin", true);
            request.setSession(session);
        }

        @Test
        @DisplayName("200 를 return 한다.")
        void it_returns_200() throws Exception {
            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertTrue(result);
            assertEquals(HttpStatus.OK.value(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("로그인한 사용자가 관리자가 아닌 경우")
    class Context_LoginUser_is_Not_Admin {
        @BeforeEach
        void setUp() {
            // given
            session.setAttribute("isAdmin", false);
            request.setSession(session);
        }

        @Test
        @DisplayName("403 을 return 한다.")
        void it_returns_403() throws Exception {
            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertFalse(result);
            assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("로그인 하지 않은 사용자인 경우")
    class Context_LoginUser_is_Not {
        @BeforeEach
        void setUp() {
            request.setSession(null);
        }

        @Test
        @DisplayName("401 를 return 한다.")
        void it_returns_401() throws Exception {
            // when
            boolean result = authInterceptor.preHandle(request, response, null);

            // then
            assertFalse(result);
            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        }
    }
}
