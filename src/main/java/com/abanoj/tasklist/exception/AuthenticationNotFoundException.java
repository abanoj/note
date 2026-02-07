package com.abanoj.tasklist.exception;

public class AuthenticationNotFoundException extends RuntimeException{
    public AuthenticationNotFoundException(String message) {
        super(message);
    }

    public AuthenticationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
