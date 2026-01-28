package com.abanoj.task_list.task.service;

import com.abanoj.task_list.task.entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<Task> findTask(Long taskListId, Long id);
    List<Task> findListTask(Long taskListId);
    Task createTask(Long taskListId, Task task);
    Task updateTask(Long taskListId, Long id, Task task);
    void deleteTask(Long taskListId, Long id);
    boolean existsById(Long taskListId, Long id);

}
