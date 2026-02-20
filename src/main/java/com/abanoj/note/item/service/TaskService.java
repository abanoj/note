package com.abanoj.note.item.service;

import com.abanoj.note.item.entity.Item;

import java.util.List;

public interface TaskService {
    Item findTask(Long checklistId, Long id);
    List<Item> findAllTasks(Long checklistId);
    Item createTask(Long checklistId, Item task);
    Item updateTask(Long checklistId, Long id, Item task);
    void deleteTask(Long checklistId, Long id);
}
