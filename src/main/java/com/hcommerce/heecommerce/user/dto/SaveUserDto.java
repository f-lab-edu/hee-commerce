package com.hcommerce.heecommerce.user.dto;

import com.hcommerce.heecommerce.user.entities.User;
import lombok.Getter;

@Getter
public class SaveUserDto {

    private String phoneNumber;

    public User of(SaveUserDto saveUserDto) {
        return new User(saveUserDto.phoneNumber);
    }
}
