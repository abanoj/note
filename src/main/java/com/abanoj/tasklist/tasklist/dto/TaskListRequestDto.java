package com.abanoj.tasklist.tasklist.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskListRequestDto(@NotBlank(message = "There must be a title") String title) {
}
