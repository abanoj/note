package com.abanoj.task_list.tasklist.service;

import com.abanoj.task_list.auth.SecurityUtils;
import com.abanoj.task_list.tasklist.entities.TaskList;
import com.abanoj.task_list.tasklist.repository.TaskListRepository;
import com.abanoj.task_list.user.User;
import com.abanoj.task_list.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<TaskList> findTaskList(Long taskListId) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        return taskListRepository.findByIdAndUser(taskListId, user);
    }

    @Override
    public List<TaskList> findAllTaskList() {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        return taskListRepository.findAllByUser(user);
    }

    @Override
    public TaskList createTaskList(TaskList taskList) {
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        if(taskList.getId() != null) throw new IllegalArgumentException("TaskList already has and ID!");
        LocalDateTime now = LocalDateTime.now();
        taskList.setCreated(now);
        taskList.setUpdated(now);
        taskList.setUser(user);
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
        String username = SecurityUtils.getCurrentUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        taskListRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id){
        return taskListRepository.existsById(id);
    }
}
