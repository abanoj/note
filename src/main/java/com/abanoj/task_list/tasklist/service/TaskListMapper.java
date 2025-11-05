package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskStatus;
import com.abanoj.task_list.task.service.TaskMapper;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.entities.TaskListDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskListMapper {

    private final TaskMapper taskMapper;

    public TaskListMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    TaskList toTaskList(TaskListDto taskListDto){
        return new TaskList(
                taskListDto.id(),
                taskListDto.title(),
                Optional.of(taskListDto.tasks().stream().map(taskMapper::toTask).toList()).orElse(null),
                null,
                null
        );
    }

    TaskListDto toTaskListDto(TaskList taskList){
        List<Task> listOfTask = taskList.getTasks();
        return new TaskListDto(
                taskList.getId(),
                taskList.getTitle(),
                Optional.of(listOfTask.size()).orElse(0),
                calculateTaskListProgress(listOfTask),
                Optional.of(listOfTask.stream().map(taskMapper::toDto).toList()).orElse(null)
        );
    }

    private Double calculateTaskListProgress(List<Task> tasks){
        if(tasks == null) return null;
        long numberOfTaskDone = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.DONE).count();
        return (double) (numberOfTaskDone / tasks.size());
    }
}
