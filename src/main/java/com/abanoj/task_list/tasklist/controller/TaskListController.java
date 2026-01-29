package com.abanoj.task_list.tasklist.controller;

import com.abanoj.task_list.exception.ResourceNotFoundException;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.entities.TaskListResponseDto;
import com.abanoj.task_list.tasklist.entities.TaskListRequestDto;
import com.abanoj.task_list.tasklist.service.TaskListMapper;
import com.abanoj.task_list.tasklist.service.TaskListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    @GetMapping
    public ResponseEntity<List<TaskListResponseDto>> getAll(){
        List<TaskListResponseDto> taskListResponseDtoList = taskListService.findAllTaskList().stream().map(taskListMapper::toTaskListDto).toList();
        return ResponseEntity.ok(taskListResponseDtoList);
    }

    @GetMapping("/{taskListId}")
    public ResponseEntity<TaskListResponseDto> getTaskList(@PathVariable("taskListId") Long id){
        TaskListResponseDto taskList = taskListService
                .findTaskList(id)
                .map(taskListMapper::toTaskListDto)
                .orElseThrow(() -> new ResourceNotFoundException("Task List with id: " + id + " not found!"));
        return ResponseEntity.ok(taskList);
    }

    @PostMapping
    public ResponseEntity<TaskListResponseDto> createTaskList(@Valid @RequestBody TaskListRequestDto taskListRequestDto){
        TaskList taskList = taskListMapper.toTaskList(taskListRequestDto);
        TaskListResponseDto taskListResponseDto = taskListMapper.toTaskListDto(taskListService.createTaskList(taskList));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskListResponseDto);
    }

    @PutMapping("/{taskListId}")
    public ResponseEntity<TaskListResponseDto> updateTaskList(@PathVariable("taskListId") Long id, @Valid @RequestBody TaskListRequestDto taskListRequestDto){
        TaskList taskList = taskListMapper.toTaskList(taskListRequestDto);
        TaskListResponseDto taskListUpdated = taskListMapper.toTaskListDto(taskListService.updateTaskList(id, taskList));
        return ResponseEntity.ok(taskListUpdated);
    }

    @DeleteMapping("/{taskListId}")
    public ResponseEntity<Void> deleteTaskList(@PathVariable("taskListId") Long id){
        if(!taskListService.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        taskListService.deleteTaskList(id);
        return ResponseEntity.noContent().build();
    }
}
