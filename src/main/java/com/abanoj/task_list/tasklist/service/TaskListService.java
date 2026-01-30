package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.tasklist.entities.TaskList;

import java.util.List;
import java.util.Optional;

public interface TaskListService {
    List<TaskList> findAllTaskList();
    TaskList findTaskList(Long taskListId);
    TaskList createTaskList(TaskList taskList);
    TaskList updateTaskList(Long id, TaskList taskList);
    boolean existsById(Long id);
    void deleteTaskList(Long id);
}
