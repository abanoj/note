package com.abanoj.note.item.service;

import com.abanoj.note.item.entity.Item;

import java.util.List;

public interface ItemService {
    Item findItem(Long checklistId, Long id);
    List<Item> findAllItems(Long checklistId);
    Item createItem(Long checklistId, Item item);
    Item updateItem(Long checklistId, Long id, Item item);
    void deleteItem(Long checklistId, Long id);
}
