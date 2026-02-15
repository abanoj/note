package com.abanoj.tasklist.tasklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskListCreateRequestDto(
        @Schema(description = "Task list title", example = "Shopping list")
        @NotBlank(message = "There must be a title") String title) {
}
