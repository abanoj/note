package com.abanoj.tasklist.auth;

import com.abanoj.tasklist.exception.AuthenticationNotFoundException;
import com.abanoj.tasklist.exception.UserNotFoundException;
import com.abanoj.tasklist.user.User;
import com.abanoj.tasklist.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public String getCurrentUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication==null || !authentication.isAuthenticated()){
            throw new AuthenticationNotFoundException("There is no authenticated user");
        }
        return authentication.getName();
    }

    public User getCurrentUser(){
        return userRepository
                .findByEmail(getCurrentUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }
}
