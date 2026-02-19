package com.abanoj.note.tasklist.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.tasklist.entity.TaskList;
import com.abanoj.note.tasklist.repository.TaskListRepository;
import com.abanoj.note.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
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
        TaskList savedTaskList = taskListRepository.save(taskList);
        log.debug("TaskList created with id {}", savedTaskList.getId());
        return savedTaskList;
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

        log.debug("TaskList {} updated", id);
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
        log.debug("TaskList {} deleted", id);
    }

}
