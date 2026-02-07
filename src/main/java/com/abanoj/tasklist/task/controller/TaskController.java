package com.abanoj.tasklist.task.controller;

import com.abanoj.tasklist.task.entity.Task;
import com.abanoj.tasklist.task.dto.TaskDto;
import com.abanoj.tasklist.task.mapper.TaskMapper;
import com.abanoj.tasklist.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task-lists/{taskListId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTask(@PathVariable("taskListId") Long taskListId){
        List<TaskDto> taskDtoList = taskService.findListTask(taskListId).stream().map(taskMapper::toTaskDto).toList();
        return ResponseEntity.ok(taskDtoList);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable("taskListId") Long taskListId, @PathVariable("taskId") Long taskId){
        Task task = taskService.findTask(taskListId, taskId);
        TaskDto taskDto = taskMapper.toTaskDto(task);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@PathVariable("taskListId") Long taskListId, @Valid @RequestBody TaskDto newTaskDto){
        Task newTask = taskService.createTask(taskListId, taskMapper.toTask(newTaskDto));
        TaskDto taskDto = taskMapper.toTaskDto(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("taskListId") Long taskListId, @PathVariable("taskId") Long taskId, @Valid @RequestBody TaskDto taskDtoToUpdate){
        Task taskUpdated = taskService.updateTask(taskListId, taskId, taskMapper.toTask(taskDtoToUpdate));
        TaskDto taskDtoUpdated = taskMapper.toTaskDto(taskUpdated);
        return ResponseEntity.ok(taskDtoUpdated);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskListId") Long taskListId, @PathVariable("taskId") Long taskId){
        if(!taskService.existsById(taskListId, taskId)){
            return ResponseEntity.notFound().build();
        }
        taskService.deleteTask(taskListId, taskId);
        return ResponseEntity.noContent().build();
    }
}
