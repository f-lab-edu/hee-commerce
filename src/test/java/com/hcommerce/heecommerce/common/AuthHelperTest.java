package com.hcommerce.heecommerce.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@DisplayName("AuthHelper")
@ExtendWith(MockitoExtension.class)
class AuthHelperTest {

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    @Nested
    @DisplayName("isLogin()")
    class Describe_isLogin {
        @Nested
        @DisplayName("with isAdmin session")
        class Context_With_isAdmin_Session {
            @Test
            @DisplayName("returns true")
            void it_returns_true() {
                // given
                given(request.getSession()).willReturn(session);
                given(session.getAttribute("isAdmin")).willReturn(false);

                // when
                boolean result = AuthHelper.isLogin(request);

                // then
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("without isAdmin session")
        class Context_Without_isAdmin_Session {
            @Test
            @DisplayName("returns false")
            void it_returns_false() {
                // given
                given(request.getSession()).willReturn(session);
                given(session.getAttribute("isAdmin")).willReturn(null);

                // when
                boolean result = AuthHelper.isLogin(request);

                // then
                assertFalse(result);
            }
        }

        @Nested
        @DisplayName("without session")
        class Context_Without_Session {
            @Test
            @DisplayName("returns false")
            void it_returns_false() {
                // given
                given(request.getSession()).willReturn(null);

                // when
                boolean result = AuthHelper.isLogin(request);

                // then
                assertFalse(result);
            }
        }
    }

    @Nested
    @DisplayName("isAdmin()")
    class Describe_isAdmin {
        @Nested
        @DisplayName("when isAdmin session returns true")
        class Context_when_isAdmin_Session_returns_true {
            @Test
            @DisplayName("returns true")
            void it_returns_true() {
                // given
                given(request.getSession()).willReturn(session);
                given(session.getAttribute("isAdmin")).willReturn(true);

                // when
                boolean result = AuthHelper.isAdmin(request);

                // then
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("when isAdmin session returns false")
        class Context_when_isAdmin_Session_returns_false {
            @Test
            @DisplayName("returns false")
            void it_returns_false() {
                // given
                given(request.getSession()).willReturn(session);
                given(session.getAttribute("isAdmin")).willReturn(false);

                // when
                boolean result = AuthHelper.isAdmin(request);
                System.out.println(result);
                // then
                assertFalse(result);
            }
        }

        @Nested
        @DisplayName("without session")
        class Context_Without_Session {
            @Test
            @DisplayName("returns false")
            void it_returns_false() {
                // given
                given(request.getSession()).willReturn(null);

                // when
                boolean result = AuthHelper.isAdmin(request);

                // then
                assertFalse(result);
            }
        }
    }
}
