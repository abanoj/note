package com.abanoj.tasklist.tasklist.mapper;

import com.abanoj.tasklist.auth.SecurityUtils;
import com.abanoj.tasklist.task.entity.Task;
import com.abanoj.tasklist.task.entity.TaskStatus;
import com.abanoj.tasklist.task.mapper.TaskMapper;
import com.abanoj.tasklist.tasklist.entity.TaskList;
import com.abanoj.tasklist.tasklist.dto.TaskListResponseDto;
import com.abanoj.tasklist.tasklist.dto.TaskListRequestDto;
import com.abanoj.tasklist.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskListMapper {

    private final TaskMapper taskMapper;
    private final SecurityUtils securityUtils;

    public TaskList toTaskList(TaskListResponseDto taskListResponseDto){
        User user = securityUtils.getCurrentUser();
        return new TaskList(
                taskListResponseDto.id(),
                taskListResponseDto.title(),
                Optional.of(taskListResponseDto.tasks().stream().map(taskMapper::toTask).toList()).orElse(null),
                null,
                null,
                user
        );
    }

    public TaskList toTaskList(TaskListRequestDto taskListRequestDto){
        return TaskList.builder().title(taskListRequestDto.title()).tasks(new ArrayList<>()).build();
    }

    public TaskListResponseDto toTaskListResponseDto(TaskList taskList){
        List<Task> listOfTask = taskList.getTasks();
        return new TaskListResponseDto(
                taskList.getId(),
                taskList.getTitle(),
                Optional.of(listOfTask.size()).orElse(0),
                calculateTaskListProgress(listOfTask),
                Optional.of(listOfTask.stream().map(taskMapper::toTaskDto).toList()).orElse(null)
        );
    }

    private Double calculateTaskListProgress(List<Task> tasks){
        if(tasks == null || tasks.isEmpty()) return null;
        long numberOfTaskDone = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.DONE).count();
        return (double) (numberOfTaskDone / tasks.size());
    }
}
