package com.hcommerce.heecommerce.common.utils;

import com.hcommerce.heecommerce.auth.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private Key key;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        if(secret == null || secret.isBlank()) { // isEmpty가 아닌 isBlank를 사용한 이유 : " " 값을 가지는 경우도 예외로 처리해야 하므로,
            // TODO : 클라이언트 - 내부 서버 예러 입니다. / 서버 로그 - jwt secret 값을 확인해주세요.
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(int userId) {
        if(userId < 0) {
            // TODO : 유효하지 않은 사용자입니다.
        }

        return Jwts.builder()
            .claim("userId", userId)
            .signWith(key)
            .compact();
    }

    public Claims decode(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (SignatureException e) { // token이 우리가 발행한 토큰이 아닌 경우
            throw new InvalidTokenException("token(value = "+token+") is invalid");
        } catch (IllegalArgumentException e) { // token이 Null 또는 "", " " 일 경우, IllegalArgumentException: JWT String argument cannot be null or empty. 발생
            throw new InvalidTokenException("token(value = null or empty) is invalid");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("token(value = "+token+") is invalid");
        } catch (Exception e) {
            throw new RuntimeException("JwtUtils : decode 예외"); // TODO : 임시로 만들어 놓음
        }
    }
}
