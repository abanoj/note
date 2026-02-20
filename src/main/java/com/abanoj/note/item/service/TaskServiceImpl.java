package com.abanoj.note.item.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.repository.TaskRepository;
import com.abanoj.note.checklist.repository.ChecklistRepository;
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
    private final ChecklistRepository checklistRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Item findTask(Long checklistId, Long id) {
        checkUserOwner(checklistId);
        return taskRepository
                .findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id: " + id + " not found"));
    }

    @Override
    public List<Item> findAllTasks(Long checklistId) {
        checkUserOwner(checklistId);
        return taskRepository.findByChecklistId(checklistId);
    }

    @Override
    @Transactional
    public Item createTask(Long checklistId, Item task) {
        Checklist checklist = checkUserOwner(checklistId);
        LocalDateTime now = LocalDateTime.now();

        Item taskToSave = new Item(
                null,
                task.getTitle(),
                task.getItemStatus(),
                task.getItemPriority(),
                checklist,
                now,
                now
        );

        Item savedTask = taskRepository.save(taskToSave);
        log.debug("Item created with id {} in checklist {}", savedTask.getId(), checklistId);
        return savedTask;
    }

    @Override
    @Transactional
    public Item updateTask(Long checklistId, Long id, Item task) {
        if(task.getId() == null) throw new IllegalArgumentException("Item must have an ID");
        if(!Objects.equals(task.getId(), id)) throw new IllegalArgumentException("ID and Item id do not match!");

        checkUserOwner(checklistId);

        Item taskToUpdate = taskRepository
                .findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found!"));

        taskToUpdate.setTitle(task.getTitle());
        taskToUpdate.setItemStatus(task.getItemStatus());
        taskToUpdate.setItemPriority(task.getItemPriority());
        taskToUpdate.setUpdated(LocalDateTime.now());
        log.debug("Item {} updated in checklist {}", id, checklistId);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public void deleteTask(Long checklistId, Long id) {
        Checklist checklist = checkUserOwner(checklistId);
        Item task = taskRepository.findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found!"));
        checklist.getTasks().remove(task);
        log.debug("Item {} deleted from checklist {}", id, checklistId);
    }

    private Checklist checkUserOwner(Long checklistId){
        User user = securityUtils.getCurrentUser();
        return checklistRepository
                .findByIdAndUser(checklistId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Checklist with id " + checklistId));
    }
}
