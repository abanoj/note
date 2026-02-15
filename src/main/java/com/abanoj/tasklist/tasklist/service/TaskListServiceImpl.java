package com.abanoj.tasklist.tasklist.service;

import com.abanoj.tasklist.auth.SecurityUtils;
import com.abanoj.tasklist.exception.ResourceNotFoundException;
import com.abanoj.tasklist.tasklist.entity.TaskList;
import com.abanoj.tasklist.tasklist.repository.TaskListRepository;
import com.abanoj.tasklist.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;
    private final SecurityUtils securityUtils;

    @Override
    public TaskList findTaskList(Long taskListId) {
        User user = securityUtils.getCurrentUser();
        return taskListRepository
                .findByIdAndUser(taskListId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task List with id: " + taskListId + " not found!"));
    }

    @Override
    public List<TaskList> findAllTaskList() {
        User user = securityUtils.getCurrentUser();
        return taskListRepository.findAllByUser(user);
    }

    @Override
    @Transactional
    public TaskList createTaskList(TaskList taskList) {
        User user = securityUtils.getCurrentUser();
        if(taskList.getId() != null) throw new IllegalArgumentException("TaskList already has and ID!");
        LocalDateTime now = LocalDateTime.now();
        taskList.setCreated(now);
        taskList.setUpdated(now);
        taskList.setUser(user);
        return taskListRepository.save(taskList);
    }

    @Override
    @Transactional
    public TaskList updateTaskList(Long id, TaskList taskList) {
        User user = securityUtils.getCurrentUser();
        if(taskList.getId() == null) throw new IllegalArgumentException("TaskList must have an ID");
        if(!Objects.equals(taskList.getId(), id)) throw new IllegalArgumentException("Id and TaskList id do not match");

        TaskList taskListToUpdate = taskListRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("TaskList with id " + id + " not found!"));

        taskListToUpdate.setTitle(taskList.getTitle());
        taskListToUpdate.setUpdated(LocalDateTime.now());

        return taskListRepository.save(taskListToUpdate);
    }

    @Override
    @Transactional
    public void deleteTaskList(Long id) {
        User user = securityUtils.getCurrentUser();
        TaskList taskList = taskListRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("TaskList with id " + id + " not found!"));
        taskListRepository.delete(taskList);
    }

}
