package com.abanoj.tasklist.tasklist.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskListCreateRequestDto(@NotBlank(message = "There must be a title") String title) {
}
