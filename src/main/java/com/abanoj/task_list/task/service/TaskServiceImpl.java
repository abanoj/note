package com.abanoj.task_list.task.service;

import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskPriority;
import com.abanoj.task_list.task.entities.TaskStatus;
import com.abanoj.task_list.task.repository.TaskRepository;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.repository.TaskListRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }

    @Override
    public Optional<Task> findTask(Long taskListId, Long id) {
        return taskRepository.findByTaskListIdAndId(taskListId, id);
    }

    @Override
    public List<Task> findListTask(Long taskListId) {
        return taskRepository.findByTaskListId(taskListId);
    }

    @Override
    public Task createTask(Long taskListId, Task task) {
        TaskList taskList = taskListRepository
                .findById(taskListId)
                .orElseThrow(() -> new IllegalArgumentException("Not found Task List wit id " + taskListId));

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
    public Task updateTask(Long taskListId, Long id, Task task) {
        if(task.getId() == null) throw new IllegalArgumentException("Task must have an ID");
        if(!Objects.equals(task.getId(), id)) throw new IllegalArgumentException("ID and Task id do not match!");

        Task taskToUpdate = taskRepository
                .findByTaskListIdAndId(taskListId, id)
                .orElseThrow(() -> new IllegalArgumentException("Task with id " + id + " not found!"));

        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setTaskStatus(task.getTaskStatus());
        taskToUpdate.setTaskPriority(task.getTaskPriority());
        taskToUpdate.setUpdated(LocalDateTime.now());

        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskListId, Long id) {
        taskRepository.deleteByTaskListIdAndId(taskListId, id);
    }
}
