package com.hcommerce.heecommerce.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("관리자인 경우")
    void testUserIsAdmin() throws Exception {
        // given
        session.setAttribute("isAdmin", true);
        request.setSession(session);

        // when
        boolean result = authInterceptor.preHandle(request, response, null);

        // then
        assertTrue(result);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @DisplayName("관리자가 아닌 경우")
    void testUserIsNotAdmin() throws Exception {
        // given
        session.setAttribute("isAdmin", false);
        request.setSession(session);

        // when
        boolean result = authInterceptor.preHandle(request, response, null);

        // then
        assertFalse(result);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("세션이 없는 경우")
    void testSessionIsNull() throws Exception {
        // given
        request.setSession(null);

        // when
        boolean result = authInterceptor.preHandle(request, response, null);

        // then
        assertFalse(result);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}
