package com.abanoj.tasklist.tasklist.controller;

import com.abanoj.tasklist.tasklist.dto.TaskListUpdateRequestDto;
import com.abanoj.tasklist.tasklist.entity.TaskList;
import com.abanoj.tasklist.tasklist.dto.TaskListResponseDto;
import com.abanoj.tasklist.tasklist.dto.TaskListCreateRequestDto;
import com.abanoj.tasklist.tasklist.mapper.TaskListMapper;
import com.abanoj.tasklist.tasklist.service.TaskListService;
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
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
@Tag(name = "Task Lists", description = "Task list management")
public class TaskListController {

    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    @GetMapping
    @Operation(summary = "Get all task lists for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task lists retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<TaskListResponseDto>> getAll(){
        List<TaskListResponseDto> taskListResponseDtoList = taskListService.findAllTaskList().stream().map(taskListMapper::toTaskListResponseDto).toList();
        return ResponseEntity.ok(taskListResponseDtoList);
    }

    @GetMapping("/{taskListId}")
    @Operation(summary = "Get a task list by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskListResponseDto> getTaskList(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long id){
        TaskList taskList = taskListService.findTaskList(id);
        TaskListResponseDto taskListResponseDto = taskListMapper.toTaskListResponseDto(taskList);
        return ResponseEntity.ok(taskListResponseDto);
    }

    @PostMapping
    @Operation(summary = "Create a new task list")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task list created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskListResponseDto> createTaskList(@Valid @RequestBody TaskListCreateRequestDto taskListRequestDto){
        TaskList taskList = taskListMapper.toTaskList(taskListRequestDto);
        TaskListResponseDto taskListResponseDto = taskListMapper.toTaskListResponseDto(taskListService.createTaskList(taskList));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskListResponseDto);
    }

    @PutMapping("/{taskListId}")
    @Operation(summary = "Update an existing task list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task list updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TaskListResponseDto> updateTaskList(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long id,
            @Valid @RequestBody TaskListUpdateRequestDto taskListRequestDto){
        TaskList taskList = taskListMapper.toTaskList(taskListRequestDto);
        TaskListResponseDto taskListUpdated = taskListMapper.toTaskListResponseDto(taskListService.updateTaskList(id, taskList));
        return ResponseEntity.ok(taskListUpdated);
    }

    @DeleteMapping("/{taskListId}")
    @Operation(summary = "Delete a task list and all its tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task list deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task list not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> deleteTaskList(
            @Parameter(description = "Task list ID") @PathVariable("taskListId") Long id){
        taskListService.deleteTaskList(id);
        return ResponseEntity.noContent().build();
    }
}
