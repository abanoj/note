package com.abanoj.task_list.task.service;

import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskDto;
import com.abanoj.task_list.task.entities.TaskPriority;
import com.abanoj.task_list.task.entities.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskMapper {
    public TaskDto toTaskDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getTaskStatus(),
                task.getTaskPriority()
        );
    }

    public Task toTask(TaskDto taskDto) {
        return new Task(
                taskDto.id(),
                taskDto.title(),
                Optional.ofNullable(taskDto.status()).orElse(TaskStatus.PENDING),
                Optional.ofNullable(taskDto.priority()).orElse(TaskPriority.MEDIUM),
                null,
                null,
                null
        );
    }
}
