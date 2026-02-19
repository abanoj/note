package com.abanoj.note.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @Schema(description = "User first name", example = "John")
    @NotBlank(message = "There must be a firstname")
    String firstname,
    @Schema(description = "User last name", example = "Doe")
    @NotBlank(message = "There must be a lastname")
    String lastname,
    @Schema(description = "User email address", example = "john.doe@email.com")
    @Email(message = "There must be a valid email")
    @NotBlank(message = "There must be a valid email")
    String email,
    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "There must be a password")
    @Size(min = 8, max = 32)
    String password
){}
