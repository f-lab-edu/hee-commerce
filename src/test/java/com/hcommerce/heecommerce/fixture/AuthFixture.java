package com.hcommerce.heecommerce.fixture;

public class AuthFixture {
    public static final String AUTH_TYPE = "Bearer";

    public static final String INVALID_AUTH_TYPE = "Invalid Bearer";

    public static final String AUTHORIZATION = AUTH_TYPE+" "+JwtFixture.TOKEN;

    public static final String AUTHORIZATION_WITH_INVALID_AUTH_TYPE = INVALID_AUTH_TYPE+" "+JwtFixture.TOKEN;

    public static final String AUTHORIZATION_WITHOUT_TOKEN = AUTH_TYPE+" ";

    public static final String AUTHORIZATION_WITHOUT_TOKEN_PAYLOAD = AUTH_TYPE+" "+JwtFixture.TOKEN_WITHOUT_TOKEN_PAYLOAD;
}
