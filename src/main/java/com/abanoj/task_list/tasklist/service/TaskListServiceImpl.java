package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.repository.TaskListRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;

    public TaskListServiceImpl(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Override
    public Optional<TaskList> findTaskList(Long taskListId) {
        return taskListRepository.findById(taskListId);
    }

    @Override
    public List<TaskList> findAllTaskList() {
        return taskListRepository.findAll();
    }

    @Override
    public TaskList createTaskList(TaskList taskList) {
        if(taskList.getId() != null) throw new IllegalArgumentException("TaskList already has and ID!");
        LocalDateTime now = LocalDateTime.now();
        taskList.setCreated(now);
        taskList.setUpdated(now);
        return taskListRepository.save(taskList);
    }

    @Override
    public TaskList updateTaskList(Long id, TaskList taskList) {
        if(taskList.getId() == null) throw new IllegalArgumentException("TaskList must have an ID");
        if(!Objects.equals(taskList.getId(), id)) throw new IllegalArgumentException("Id and TaskList id do not match");

        TaskList taskListToUpdate = taskListRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("TaskList with id " + id + " not found!"));

        taskListToUpdate.setTitle(taskList.getTitle());
        taskListToUpdate.setUpdated(LocalDateTime.now());

        return taskListRepository.save(taskListToUpdate);
    }

    @Override
    public void deleteTaskList(Long id) {
        taskListRepository.deleteById(id);
    }
}
