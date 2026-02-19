package com.abanoj.note.tasklist.service;

import com.abanoj.note.tasklist.entity.TaskList;

import java.util.List;

public interface TaskListService {
    List<TaskList> findAllTaskList();
    TaskList findTaskList(Long taskListId);
    TaskList createTaskList(TaskList taskList);
    TaskList updateTaskList(Long id, TaskList taskList);
    void deleteTaskList(Long id);
}
