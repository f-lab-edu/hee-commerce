package com.hcommerce.heecommerce.fixture;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtFixture {

    public static final String SECRET = "12345678901234567890123456789012";

    public static final String INVALID_SECRET = "11234567890123456789012345678901";

    // userId : userId = 1이고, secret 이 우리 형식일 때의 값
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    public static final String TOKEN_WITHOUT_TOKEN_PAYLOAD = "eyJhbGciOiJIUzI1NiJ9..ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    // INVALID_TOKEN : userId = 1이지만, secret 이 우리 형식이 아닌 secret일 때의 값
    public static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    public static Claims CLAIMS = Jwts.parserBuilder()
                                    .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                                    .build()
                                    .parseClaimsJws(TOKEN)
                                    .getBody();

}
