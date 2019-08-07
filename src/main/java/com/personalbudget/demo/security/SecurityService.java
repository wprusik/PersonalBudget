package com.personalbudget.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    
    public String getUsernameFromSecurityContext() {        
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
