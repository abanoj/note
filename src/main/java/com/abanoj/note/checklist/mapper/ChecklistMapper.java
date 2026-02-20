package com.abanoj.note.checklist.mapper;

import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.item.mapper.ItemMapper;
import com.abanoj.note.checklist.dto.ChecklistUpdateRequestDto;
import com.abanoj.note.checklist.dto.ChecklistResponseDto;
import com.abanoj.note.checklist.dto.ChecklistCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChecklistMapper {

    private final ItemMapper itemMapper;

    public Checklist toChecklist(ChecklistCreateRequestDto checklistRequestDto){
        return Checklist.builder().title(checklistRequestDto.title()).items(new ArrayList<>()).build();
    }

    public Checklist toChecklist(ChecklistUpdateRequestDto checklistUpdateRequestDto){
        return Checklist.builder()
                .id(checklistUpdateRequestDto.id())
                .title(checklistUpdateRequestDto.title())
                .build();
    }

    public ChecklistResponseDto toChecklistResponseDto(Checklist checklist){
        List<Item> listOfItems = checklist.getItems();
        return new ChecklistResponseDto(
                checklist.getId(),
                checklist.getTitle(),
                listOfItems.size(),
                calculateChecklistProgress(listOfItems),
                listOfItems.stream().map(itemMapper::toItemDto).toList()
        );
    }

    private Double calculateChecklistProgress(List<Item> items){
        if(items == null || items.isEmpty()) return null;
        long numberOfItemsDone = items.stream().filter(item -> item.getItemStatus() == ItemStatus.DONE).count();
        return (double) numberOfItemsDone / items.size();
    }
}
