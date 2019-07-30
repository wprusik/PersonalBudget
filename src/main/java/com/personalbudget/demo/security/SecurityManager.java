package com.personalbudget.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityManager {
    
    public String getUsernameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
