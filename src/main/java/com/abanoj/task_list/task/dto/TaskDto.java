package com.abanoj.task_list.task.dto;

import com.abanoj.task_list.task.entity.TaskPriority;
import com.abanoj.task_list.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskDto(
        Long id,
        @NotBlank(message = "There must be a title") String title,
        TaskStatus status,
        TaskPriority priority) {
}
