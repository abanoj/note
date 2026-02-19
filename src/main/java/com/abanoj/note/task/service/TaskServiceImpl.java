package com.abanoj.note.task.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.task.entity.Task;
import com.abanoj.note.task.repository.TaskRepository;
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
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Task findTask(Long taskListId, Long id) {
        checkUserOwner(taskListId);
        return taskRepository
                .findByTaskListIdAndId(taskListId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id: " + id + " not found"));
    }

    @Override
    public List<Task> findListTask(Long taskListId) {
        checkUserOwner(taskListId);
        return taskRepository.findByTaskListId(taskListId);
    }

    @Override
    @Transactional
    public Task createTask(Long taskListId, Task task) {
        TaskList taskList = checkUserOwner(taskListId);
        LocalDateTime now = LocalDateTime.now();

        Task taskToSave = new Task(
                null,
                task.getTitle(),
                task.getTaskStatus(),
                task.getTaskPriority(),
                taskList,
                now,
                now
        );

        Task savedTask = taskRepository.save(taskToSave);
        log.debug("Task created with id {} in taskList {}", savedTask.getId(), taskListId);
        return savedTask;
    }

    @Override
    @Transactional
    public Task updateTask(Long taskListId, Long id, Task task) {
        if(task.getId() == null) throw new IllegalArgumentException("Task must have an ID");
        if(!Objects.equals(task.getId(), id)) throw new IllegalArgumentException("ID and Task id do not match!");

        checkUserOwner(taskListId);

        Task taskToUpdate = taskRepository
                .findByTaskListIdAndId(taskListId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));

        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setTaskStatus(task.getTaskStatus());
        taskToUpdate.setTaskPriority(task.getTaskPriority());
        taskToUpdate.setUpdated(LocalDateTime.now());
        log.debug("Task {} updated in taskList {}", id, taskListId);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskListId, Long id) {
        checkUserOwner(taskListId);
        Task task = taskRepository.findByTaskListIdAndId(taskListId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        taskRepository.delete(task);
        log.debug("Task {} deleted from taskList {}", id, taskListId);
    }

    private TaskList checkUserOwner(Long taskListId){
        User user = securityUtils.getCurrentUser();
        TaskList taskList = taskListRepository
                .findById(taskListId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Task List with id " + taskListId));

        if(!user.equals(taskList.getUser())){
            throw new ResourceNotFoundException("Not found Task List with id " + taskListId);
        }
        return taskList;
    }
}
