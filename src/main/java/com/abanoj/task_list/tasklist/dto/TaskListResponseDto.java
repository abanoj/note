package com.abanoj.task_list.tasklist.dto;

import com.abanoj.task_list.task.dto.TaskDto;

import java.util.List;

public record TaskListResponseDto(
        Long id,
        String title,
        Integer numberOfTask,
        Double progress,
        List<TaskDto> tasks
) {
}
