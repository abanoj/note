package com.abanoj.task_list.task.controller;

import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskDto;
import com.abanoj.task_list.task.service.TaskMapper;
import com.abanoj.task_list.task.service.TaskService;
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
        List<TaskDto> taskDtoList = taskService.findListTask(taskListId).stream().map(taskMapper::toDto).toList();
        return ResponseEntity.ok(taskDtoList);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable("taskListId") Long taskListId, @PathVariable("taskId") Long taskId){
        TaskDto taskDto = taskService
                .findTask(taskListId, taskId).map(taskMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Task with id: " + taskId + " not found"));
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@PathVariable("taskListId") Long taskListId, @Valid @RequestBody TaskDto newTaskDto){
        Task newTask = taskService.createTask(taskListId, taskMapper.toTask(newTaskDto));
        TaskDto taskDto = taskMapper.toDto(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("taskListId") Long taskListId, @PathVariable("taskId") Long taskId, @Valid @RequestBody TaskDto taskDtoToUpdate){
        Task taskUpdated = taskService.updateTask(taskListId, taskId, taskMapper.toTask(taskDtoToUpdate));
        TaskDto taskDtoUpdated = taskMapper.toDto(taskUpdated);
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
