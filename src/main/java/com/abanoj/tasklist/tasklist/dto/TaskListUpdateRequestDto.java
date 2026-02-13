package com.abanoj.tasklist.tasklist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskListUpdateRequestDto(
        @NotNull(message = "There must be an ID")
        Long id,
        @NotBlank(message = "There must be a title")
        String title) {
}
