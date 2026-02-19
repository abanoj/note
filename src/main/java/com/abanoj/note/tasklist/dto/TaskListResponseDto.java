package com.abanoj.note.tasklist.dto;

import com.abanoj.note.task.dto.TaskDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TaskListResponseDto(
        @Schema(description = "Task list ID", example = "1")
        Long id,
        @Schema(description = "Task list title", example = "Shopping list")
        String title,
        @Schema(description = "Total number of tasks", example = "5")
        Integer numberOfTask,
        @Schema(description = "Completion progress (0.0 to 1.0)", example = "6.0")
        Double progress,
        @Schema(description = "List of tasks")
        List<TaskDto> tasks
) {
}
