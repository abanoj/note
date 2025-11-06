package com.abanoj.task_list.tasklist.controller;

import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.entities.TaskListDto;
import com.abanoj.task_list.tasklist.service.TaskListMapper;
import com.abanoj.task_list.tasklist.service.TaskListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/task-list/")
public class TaskListController {

    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    public TaskListController(TaskListService taskListService, TaskListMapper taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping
    public List<TaskListDto> getAll(){
        return taskListService.findAllTaskList().stream().map(taskListMapper::toTaskListDto).toList();
    }

    @GetMapping("/{task-list-id}")
    public Optional<TaskListDto> getTaskList(@PathVariable("task-list-id") Long id){
        return taskListService.findTaskList(id).map(taskListMapper::toTaskListDto);
    }

    @PostMapping
    public TaskListDto createTaskList(@RequestBody TaskListDto taskListDto){
        TaskList taskList = taskListMapper.toTaskList(taskListDto);
        return taskListMapper
                .toTaskListDto(taskListService.createTaskList(taskList));
    }

    @PutMapping("/{task-list-id}")
    public TaskListDto updateTaskList(@PathVariable("task-list-id") Long id, @RequestBody TaskListDto taskListDto){
        TaskList taskList = taskListMapper.toTaskList(taskListDto);
        return taskListMapper
                .toTaskListDto(taskListService.updateTaskList(id, taskList));
    }

    @DeleteMapping("/{task-list-id}")
    public void deleteTaskList(@PathVariable("task-list-id") Long id){
        taskListService.deleteTaskList(id);
    }
}
