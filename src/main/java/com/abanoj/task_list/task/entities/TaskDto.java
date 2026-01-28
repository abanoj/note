package com.abanoj.task_list.task.entities;

import jakarta.validation.constraints.NotBlank;

public record TaskDto(
        Long id,
        @NotBlank(message = "There must be a title") String title,
        TaskStatus status,
        TaskPriority priority) {
}
