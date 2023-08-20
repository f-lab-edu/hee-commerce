package com.hcommerce.heecommerce.fixture;

public class JwtFixture {

    public static final String SECRET = "12345678901234567890123456789012";

    public static final String INVALID_SECRET = "11234567890123456789012345678901";

    // userId : userId = 1이지만,
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    // INVALID_TOKEN : userId = 1이지만, secret 이 우리 형식이 아닌 secret일 때의 값
    public static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
}
