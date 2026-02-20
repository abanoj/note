package com.abanoj.note.item.mapper;

import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.dto.ItemDto;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getTitle(),
                item.getItemStatus(),
                item.getItemPriority()
        );
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.id(),
                itemDto.title(),
                Optional.ofNullable(itemDto.status()).orElse(ItemStatus.PENDING),
                Optional.ofNullable(itemDto.priority()).orElse(ItemPriority.MEDIUM),
                null,
                null,
                null
        );
    }
}
