package com.personalbudget.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePassword {

    public static void main(String[] args) {        
        String password = "test";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword  = passwordEncoder.encode(password);
        
        System.out.println(hashedPassword);
        password = "admin";
        hashedPassword = passwordEncoder.encode(password);
        
        System.out.println(hashedPassword);        
    }
}
