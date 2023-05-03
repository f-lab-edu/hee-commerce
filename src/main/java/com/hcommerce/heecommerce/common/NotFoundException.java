package com.hcommerce.heecommerce.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("데이터를 찾을 수 없습니다.");
    }

    public NotFoundException(UUID uuid) {
        super(uuid+" : 해당하는 데이터를 찾을 수 없습니다.");
    }
}
