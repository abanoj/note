package com.abanoj.task_list.exception;

public class AuthenticationNotFoundException extends RuntimeException{
    public AuthenticationNotFoundException(String message) {
        super(message);
    }

    public AuthenticationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
