package com.abanoj.task_list.tasklist.entities;

import jakarta.validation.constraints.NotBlank;

public record TaskListRequestDto(@NotBlank(message = "There must be a title") String title) {
}
