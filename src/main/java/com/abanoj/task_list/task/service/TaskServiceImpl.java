package com.abanoj.task_list.task.service;

import com.abanoj.task_list.auth.SecurityUtils;
import com.abanoj.task_list.task.entities.Task;
import com.abanoj.task_list.task.entities.TaskPriority;
import com.abanoj.task_list.task.entities.TaskStatus;
import com.abanoj.task_list.task.repository.TaskRepository;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.repository.TaskListRepository;
import com.abanoj.task_list.user.User;
import com.abanoj.task_list.user.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public Optional<Task> findTask(Long taskListId, Long id) {
        checkUserOwner(taskListId);
        return taskRepository.findByTaskListIdAndId(taskListId, id);
    }

    @Override
    public List<Task> findListTask(Long taskListId) {
        checkUserOwner(taskListId);
        return taskRepository.findByTaskListId(taskListId);
    }

    @Override
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
    public Task updateTask(Long taskListId, Long id, Task task) {
        if(task.getId() == null) throw new IllegalArgumentException("Task must have an ID");
        if(!Objects.equals(task.getId(), id)) throw new IllegalArgumentException("ID and Task id do not match!");

        checkUserOwner(taskListId);

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
        checkUserOwner(taskListId);
        taskRepository.deleteByTaskListIdAndId(taskListId, id);
    }

    @Override
    public boolean existsById(Long taskListId, Long id) {
        checkUserOwner(taskListId);
        return taskRepository.existsById(id);
    }


    private TaskList checkUserOwner(Long taskListId){
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        TaskList taskList = taskListRepository
                .findById(taskListId)
                .orElseThrow(() -> new IllegalArgumentException("Not found Task List wit id " + taskListId));

        if(!user.equals(taskList.getUser())){
            throw new IllegalArgumentException("Not found Task List wit id " + taskListId);
        }
        return taskList;
    }
}
