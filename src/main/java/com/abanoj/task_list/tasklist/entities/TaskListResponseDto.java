package com.abanoj.task_list.tasklist.entities;

import com.abanoj.task_list.task.entities.TaskDto;

import java.util.List;

public record TaskListResponseDto(
        Long id,
        String title,
        Integer numberOfTask,
        Double progress,
        List<TaskDto> tasks
) {
}
