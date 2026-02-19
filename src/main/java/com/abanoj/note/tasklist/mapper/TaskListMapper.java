package com.abanoj.note.tasklist.mapper;

import com.abanoj.note.task.entity.Task;
import com.abanoj.note.task.entity.TaskStatus;
import com.abanoj.note.task.mapper.TaskMapper;
import com.abanoj.note.tasklist.dto.TaskListUpdateRequestDto;
import com.abanoj.note.tasklist.entity.TaskList;
import com.abanoj.note.tasklist.dto.TaskListResponseDto;
import com.abanoj.note.tasklist.dto.TaskListCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskListMapper {

    private final TaskMapper taskMapper;

    public TaskList toTaskList(TaskListCreateRequestDto taskListRequestDto){
        return TaskList.builder().title(taskListRequestDto.title()).tasks(new ArrayList<>()).build();
    }

    public TaskList toTaskList(TaskListUpdateRequestDto taskListUpdateRequestDto){
        return TaskList.builder()
                .id(taskListUpdateRequestDto.id())
                .title(taskListUpdateRequestDto.title())
                .build();
    }

    public TaskListResponseDto toTaskListResponseDto(TaskList taskList){
        List<Task> listOfTask = taskList.getTasks();
        return new TaskListResponseDto(
                taskList.getId(),
                taskList.getTitle(),
                listOfTask.size(),
                calculateTaskListProgress(listOfTask),
                listOfTask.stream().map(taskMapper::toTaskDto).toList()
        );
    }

    private Double calculateTaskListProgress(List<Task> tasks){
        if(tasks == null || tasks.isEmpty()) return null;
        long numberOfTaskDone = tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.DONE).count();
        return (double) numberOfTaskDone / tasks.size();
    }
}
