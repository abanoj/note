package com.abanoj.note.item.controller;

import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.dto.TaskDto;
import com.abanoj.note.item.mapper.TaskMapper;
import com.abanoj.note.item.service.TaskService;
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
@RequestMapping("/api/v1/checklists/{taskListId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Item management within a checklist")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    @Operation(summary = "Get all tasks from a item list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<TaskDto>> getAllTask(
            @Parameter(description = "Item list ID") @PathVariable("taskListId") Long taskListId){
        List<TaskDto> taskDtoList = taskService.findAllTasks(taskListId).stream().map(taskMapper::toTaskDto).toList();
        return ResponseEntity.ok(taskDtoList);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get a item by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Item or item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> getTask(
            @Parameter(description = "Item list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Item ID") @PathVariable("taskId") Long taskId){
        Item task = taskService.findTask(taskListId, taskId);
        TaskDto taskDto = taskMapper.toTaskDto(task);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping
    @Operation(summary = "Create a new item")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> createTask(
            @Parameter(description = "Item list ID") @PathVariable("taskListId") Long taskListId,
            @Valid @RequestBody TaskDto newTaskDto){
        Item newTask = taskService.createTask(taskListId, taskMapper.toTask(newTaskDto));
        TaskDto taskDto = taskMapper.toTaskDto(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update an existing item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Item or item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskDto> updateTask(
            @Parameter(description = "Item list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Item ID") @PathVariable("taskId") Long taskId,
            @Valid @RequestBody TaskDto taskDtoToUpdate){
        Item taskUpdated = taskService.updateTask(taskListId, taskId, taskMapper.toTask(taskDtoToUpdate));
        TaskDto taskDtoUpdated = taskMapper.toTaskDto(taskUpdated);
        return ResponseEntity.ok(taskDtoUpdated);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete a item")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Item or item list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Item list ID") @PathVariable("taskListId") Long taskListId,
            @Parameter(description = "Item ID") @PathVariable("taskId") Long taskId){
        taskService.deleteTask(taskListId, taskId);
        return ResponseEntity.noContent().build();
    }
}
