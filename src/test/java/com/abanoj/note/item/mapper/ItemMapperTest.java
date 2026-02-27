package com.abanoj.note.item.mapper;

import com.abanoj.note.item.dto.ItemDto;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    void toItemDtoShouldMapAllFields() {
        Item item = new Item(1L, "Buy milk", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        ItemDto dto = itemMapper.toItemDto(item);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Buy milk");
        assertThat(dto.status()).isEqualTo(ItemStatus.PENDING);
        assertThat(dto.priority()).isEqualTo(ItemPriority.HIGH);
    }

    @Test
    void toItemShouldMapAllFields() {
        ItemDto dto = new ItemDto(1L, "Buy milk", ItemStatus.DONE, ItemPriority.LOW);

        Item item = itemMapper.toItem(dto);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getTitle()).isEqualTo("Buy milk");
        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.DONE);
        assertThat(item.getItemPriority()).isEqualTo(ItemPriority.LOW);
    }

    @Test
    void toItemShouldDefaultStatusToPendingWhenNull() {
        ItemDto dto = new ItemDto(null, "Task", null, ItemPriority.HIGH);

        Item item = itemMapper.toItem(dto);

        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.PENDING);
    }

    @Test
    void toItemShouldDefaultPriorityToMediumWhenNull() {
        ItemDto dto = new ItemDto(null, "Task", ItemStatus.IN_PROGRESS, null);

        Item item = itemMapper.toItem(dto);

        assertThat(item.getItemPriority()).isEqualTo(ItemPriority.MEDIUM);
    }

    @Test
    void toItemShouldDefaultBothStatusAndPriorityWhenNull() {
        ItemDto dto = new ItemDto(null, "Task", null, null);

        Item item = itemMapper.toItem(dto);

        assertThat(item.getItemStatus()).isEqualTo(ItemStatus.PENDING);
        assertThat(item.getItemPriority()).isEqualTo(ItemPriority.MEDIUM);
    }
}
