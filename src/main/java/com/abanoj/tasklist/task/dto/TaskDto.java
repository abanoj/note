package com.abanoj.tasklist.task.dto;

import com.abanoj.tasklist.task.entity.TaskPriority;
import com.abanoj.tasklist.task.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TaskDto(
        @Schema(description = "Task ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,
        @Schema(description = "Task title", example = "Buy groceries")
        @NotBlank(message = "There must be a title") String title,
        @Schema(description = "Task status", example = "PENDING")
        TaskStatus status,
        @Schema(description = "Task priority", example = "HIGH")
        TaskPriority priority) {
}
