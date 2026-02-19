package com.abanoj.note.task.mapper;

import com.abanoj.note.task.entity.Task;
import com.abanoj.note.task.dto.TaskDto;
import com.abanoj.note.task.entity.TaskPriority;
import com.abanoj.note.task.entity.TaskStatus;
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
