package com.hcommerce.heecommerce.user.entities;

import lombok.Getter;

@Getter
public class User {

    private Long id;

    private String phoneNumber;

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
