package com.abanoj.note.item.mapper;

import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.dto.TaskDto;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskMapper {
    public TaskDto toTaskDto(Item task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getItemStatus(),
                task.getItemPriority()
        );
    }

    public Item toTask(TaskDto taskDto) {
        return new Item(
                taskDto.id(),
                taskDto.title(),
                Optional.ofNullable(taskDto.status()).orElse(ItemStatus.PENDING),
                Optional.ofNullable(taskDto.priority()).orElse(ItemPriority.MEDIUM),
                null,
                null,
                null
        );
    }
}
