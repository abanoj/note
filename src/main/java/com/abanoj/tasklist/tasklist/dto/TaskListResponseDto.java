package com.abanoj.tasklist.tasklist.dto;

import com.abanoj.tasklist.task.dto.TaskDto;

import java.util.List;

public record TaskListResponseDto(
        Long id,
        String title,
        Integer numberOfTask,
        Double progress,
        List<TaskDto> tasks
) {
}
