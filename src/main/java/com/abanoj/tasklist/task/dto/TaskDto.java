package com.abanoj.tasklist.task.dto;

import com.abanoj.tasklist.task.entity.TaskPriority;
import com.abanoj.tasklist.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskDto(
        Long id,
        @NotBlank(message = "There must be a title") String title,
        TaskStatus status,
        TaskPriority priority) {
}
