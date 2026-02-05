package com.abanoj.task_list.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "There must be a firstname")
    private String firstname;
    @NotBlank(message = "There must be a lastname")
    private String lastname;
    @Email(message = "There must be a valid email")
    private String email;
    @NotBlank(message = "There must be a password")
    private String password;
}
