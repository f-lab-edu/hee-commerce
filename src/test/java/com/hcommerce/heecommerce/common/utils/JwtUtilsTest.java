package com.hcommerce.heecommerce.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hcommerce.heecommerce.auth.InvalidTokenException;
import com.hcommerce.heecommerce.fixture.JwtFixture;
import com.hcommerce.heecommerce.fixture.UserFixture;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("JwtUtils")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(JwtFixture.SECRET);
    }

    @Nested
    @DisplayName("encode")
    class Describe_Encode {
        @Test
        @DisplayName("returns token")
        void It_returns_token() {
            String token = jwtUtils.encode(UserFixture.ID);

            assertThat(token).isEqualTo(JwtFixture.TOKEN);
        }
    }

    @Nested
    @DisplayName("decode")
    class Describe_Decode {
        @Nested
        @DisplayName("with valid token")
        class Context_With_Valid_Token {
            @Test
            @DisplayName("returns claims with `userId`")
            void It_returns_token() {
                Claims claims = jwtUtils.decode(JwtFixture.TOKEN);

                assertThat(claims.get("userId", Integer.class)).isEqualTo(UserFixture.ID);
            }
        }

        @Nested
        @DisplayName("with invalid token")
        class Context_With_Invalid_Token {
            @Test
            @DisplayName("throws InvalidTokenException")
            void It_throws_InvalidTokenException() {
                assertThrows(InvalidTokenException.class, () -> jwtUtils.decode(JwtFixture.INVALID_TOKEN));
            }
        }
    }
}