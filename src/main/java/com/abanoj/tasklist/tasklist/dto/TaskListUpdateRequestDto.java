package com.abanoj.tasklist.tasklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskListUpdateRequestDto(
        @Schema(description = "Task list ID", example = "1")
        @NotNull(message = "There must be an ID")
        Long id,
        @Schema(description = "Task list title", example = "Shopping list")
        @NotBlank(message = "There must be a title")
        String title) {
}
