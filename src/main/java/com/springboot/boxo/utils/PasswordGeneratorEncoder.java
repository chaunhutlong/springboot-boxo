package com.springboot.boxo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class PasswordGeneratorEncoder {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for (String s : Arrays.asList("admin", "admin@123")) {
            System.out.println(passwordEncoder.encode(s));
        }
    }
}
