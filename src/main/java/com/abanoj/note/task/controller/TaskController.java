package com.abanoj.note.task.controller;

import com.abanoj.note.task.entity.Task;
import com.abanoj.note.task.dto.TaskDto;
import com.abanoj.note.task.mapper.TaskMapper;
import com.abanoj.note.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task-lists/{taskListId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management within a task list")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    @Operation(summary = "Get all tasks from a task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<TaskDto>> getAllTask(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long taskListId){
        List<TaskDto> taskDtoList = taskService.findListTask(taskListId).stream().map(taskMapper::toTaskDto).toList();
        return ResponseEntity.ok(taskDtoList);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task or task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> getTask(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Task ID") @PathVariable("taskId") Long taskId){
        Task task = taskService.findTask(taskListId, taskId);
        TaskDto taskDto = taskMapper.toTaskDto(task);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> createTask(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long taskListId,
            @Valid @RequestBody TaskDto newTaskDto){
        Task newTask = taskService.createTask(taskListId, taskMapper.toTask(newTaskDto));
        TaskDto taskDto = taskMapper.toTaskDto(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Task or task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Task ID") @PathVariable("taskId") Long taskId,
            @Valid @RequestBody TaskDto taskDtoToUpdate){
        Task taskUpdated = taskService.updateTask(taskListId, taskId, taskMapper.toTask(taskDtoToUpdate));
        TaskDto taskDtoUpdated = taskMapper.toTaskDto(taskUpdated);
        return ResponseEntity.ok(taskDtoUpdated);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task or task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Task ID") @PathVariable("taskId") Long taskId){
        taskService.deleteTask(taskListId, taskId);
        return ResponseEntity.noContent().build();
    }
}
