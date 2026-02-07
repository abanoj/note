package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.tasklist.entity.TaskList;

import java.util.List;

public interface TaskListService {
    List<TaskList> findAllTaskList();
    TaskList findTaskList(Long taskListId);
    TaskList createTaskList(TaskList taskList);
    TaskList updateTaskList(Long id, TaskList taskList);
    boolean existsById(Long id);
    void deleteTaskList(Long id);
}
