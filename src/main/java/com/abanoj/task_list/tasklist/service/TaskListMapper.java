package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.auth.SecurityUtils;
import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskStatus;
import com.abanoj.task_list.task.service.TaskMapper;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.entities.TaskListResponseDto;
import com.abanoj.task_list.tasklist.entities.TaskListRequestDto;
import com.abanoj.task_list.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskListMapper {

    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public TaskList toTaskList(TaskListResponseDto taskListResponseDto){
        String username = SecurityUtils.getCurrentUsername();
        return new TaskList(
                taskListResponseDto.id(),
                taskListResponseDto.title(),
                Optional.of(taskListResponseDto.tasks().stream().map(taskMapper::toTask).toList()).orElse(null),
                null,
                null,
                userRepository.findByEmail(username).orElseThrow()
        );
    }

    public TaskList toTaskList(TaskListRequestDto taskListRequestDto){
        return TaskList.builder().title(taskListRequestDto.title()).tasks(new ArrayList<>()).build();
    }

    public TaskListResponseDto toTaskListDto(TaskList taskList){
        List<Task> listOfTask = taskList.getTasks();
        return new TaskListResponseDto(
                taskList.getId(),
                taskList.getTitle(),
                Optional.of(listOfTask.size()).orElse(0),
                calculateTaskListProgress(listOfTask),
                Optional.of(listOfTask.stream().map(taskMapper::toDto).toList()).orElse(null)
        );
    }

    private Double calculateTaskListProgress(List<Task> tasks){
        if(tasks == null || tasks.isEmpty()) return null;
        long numberOfTaskDone = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.DONE).count();
        return (double) (numberOfTaskDone / tasks.size());
    }
}
