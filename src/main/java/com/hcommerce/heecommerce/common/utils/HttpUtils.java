package com.hcommerce.heecommerce.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpUtils<T> {

    public static HttpEntity<String> createHttpEntity(HttpHeaders headers, Object body) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String bodyString = objectMapper.writeValueAsString(body);

            return new HttpEntity<>(bodyString, headers);
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
