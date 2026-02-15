package com.abanoj.tasklist.task.service;

import com.abanoj.tasklist.task.entity.Task;

import java.util.List;

public interface TaskService {
    Task findTask(Long taskListId, Long id);
    List<Task> findListTask(Long taskListId);
    Task createTask(Long taskListId, Task task);
    Task updateTask(Long taskListId, Long id, Task task);
    void deleteTask(Long taskListId, Long id);
}
