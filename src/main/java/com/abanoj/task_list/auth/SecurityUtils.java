package com.abanoj.task_list.auth;

import com.abanoj.task_list.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public static String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication==null || !authentication.isAuthenticated()){
            throw new RuntimeException("There is no authenticated user");
        }
        return authentication.getName();
    }
}
