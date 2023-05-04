package com.hcommerce.heecommerce.auth;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

public class CryptoHelper {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
