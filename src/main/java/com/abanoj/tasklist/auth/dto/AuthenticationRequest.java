package com.abanoj.tasklist.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest (
    @Schema(description = "User email address", example = "john.doe@email.com")
    @Email(message = "There must be a valid email")
    @NotBlank(message = "There must be a valid email")
    String email,
    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "There must be a password")
    String password
){}
