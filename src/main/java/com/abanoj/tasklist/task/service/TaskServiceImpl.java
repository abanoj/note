package com.abanoj.tasklist.task.service;

import com.abanoj.tasklist.auth.SecurityUtils;
import com.abanoj.tasklist.exception.ResourceNotFoundException;
import com.abanoj.tasklist.task.entity.Task;
import com.abanoj.tasklist.task.entity.TaskPriority;
import com.abanoj.tasklist.task.entity.TaskStatus;
import com.abanoj.tasklist.task.repository.TaskRepository;
import com.abanoj.tasklist.tasklist.entity.TaskList;
import com.abanoj.tasklist.tasklist.repository.TaskListRepository;
import com.abanoj.tasklist.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        TaskStatus taskStatus = Optional.ofNullable(task.getTaskStatus()).orElse(TaskStatus.PENDING);
        TaskPriority taskPriority = Optional.ofNullable(task.getTaskPriority()).orElse(TaskPriority.MEDIUM);
        LocalDateTime now = LocalDateTime.now();

        Task taskToSave = new Task(
                null,
                task.getTitle(),
                taskStatus,
                taskPriority,
                taskList,
                now,
                now
        );

        return taskRepository.save(taskToSave);
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

        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskListId, Long id) {
        existsById(taskListId, id);
        taskRepository.deleteByTaskListIdAndId(taskListId, id);
    }

    @Override
    public boolean existsById(Long taskListId, Long id) {
        checkUserOwner(taskListId);
        return taskRepository.existsById(id);
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
