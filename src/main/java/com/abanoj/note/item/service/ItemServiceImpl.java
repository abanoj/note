package com.abanoj.note.item.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ChecklistRepository checklistRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Item findItem(Long checklistId, Long id) {
        checkUserOwner(checklistId);
        return itemRepository
                .findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id: " + id + " not found"));
    }

    @Override
    public List<Item> findAllItems(Long checklistId) {
        checkUserOwner(checklistId);
        return itemRepository.findByChecklistId(checklistId);
    }

    @Override
    @Transactional
    public Item createItem(Long checklistId, Item item) {
        Checklist checklist = checkUserOwner(checklistId);
        LocalDateTime now = LocalDateTime.now();

        Item itemToSave = new Item(
                null,
                item.getTitle(),
                item.getItemStatus(),
                item.getItemPriority(),
                checklist,
                now,
                now
        );

        Item savedItem = itemRepository.save(itemToSave);
        log.debug("Item created with id {} in checklist {}", savedItem.getId(), checklistId);
        return savedItem;
    }

    @Override
    @Transactional
    public Item updateItem(Long checklistId, Long id, Item item) {
        if(item.getId() == null) throw new IllegalArgumentException("Item must have an ID");
        if(!Objects.equals(item.getId(), id)) throw new IllegalArgumentException("ID and Item id do not match!");

        checkUserOwner(checklistId);

        Item itemToUpdate = itemRepository
                .findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found!"));

        itemToUpdate.setTitle(item.getTitle());
        itemToUpdate.setItemStatus(item.getItemStatus());
        itemToUpdate.setItemPriority(item.getItemPriority());
        itemToUpdate.setUpdated(LocalDateTime.now());
        log.debug("Item {} updated in checklist {}", id, checklistId);
        return itemRepository.save(itemToUpdate);
    }

    @Override
    @Transactional
    public void deleteItem(Long checklistId, Long id) {
        Checklist checklist = checkUserOwner(checklistId);
        Item item = itemRepository.findByChecklistIdAndId(checklistId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found!"));
        checklist.getItems().remove(item);
        log.debug("Item {} deleted from checklist {}", id, checklistId);
    }

    private Checklist checkUserOwner(Long checklistId){
        User user = securityUtils.getCurrentUser();
        return checklistRepository
                .findByIdAndUser(checklistId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Checklist with id " + checklistId));
    }
}
