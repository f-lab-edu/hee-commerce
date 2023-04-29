package com.hcommerce.heecommerce.user.dto.response;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserSignUpResponseDto {

    private Long id;


    public UserSignUpResponseDto(Long id) {
        this.id = id;
    }
}
