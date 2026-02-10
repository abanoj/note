package com.abanoj.tasklist.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest (
    @Email(message = "There must be a valid email")
    @NotBlank(message = "There must be a valid email")
    String email,
    @NotBlank(message = "There must be a password")
    String password
){}
