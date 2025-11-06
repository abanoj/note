package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.tasklist.entities.TaskList;

import java.util.List;
import java.util.Optional;

public interface TaskListService {
    Optional<TaskList> findTaskList(Long taskListId);
    List<TaskList> findAllTaskList();
    TaskList createTaskList(TaskList taskList);
    TaskList updateTaskList(Long id, TaskList taskList);
    void deleteTaskList(Long id);
}
