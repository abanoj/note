package com.abanoj.note.checklist.mapper;

import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.item.mapper.TaskMapper;
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

    private final TaskMapper taskMapper;

    public Checklist toChecklist(ChecklistCreateRequestDto checklistRequestDto){
        return Checklist.builder().title(checklistRequestDto.title()).tasks(new ArrayList<>()).build();
    }

    public Checklist toChecklist(ChecklistUpdateRequestDto checklistUpdateRequestDto){
        return Checklist.builder()
                .id(checklistUpdateRequestDto.id())
                .title(checklistUpdateRequestDto.title())
                .build();
    }

    public ChecklistResponseDto toChecklistResponseDto(Checklist checklist){
        List<Item> listOfItem = checklist.getTasks();
        return new ChecklistResponseDto(
                checklist.getId(),
                checklist.getTitle(),
                listOfItem.size(),
                calculateChecklistProgress(listOfItem),
                listOfItem.stream().map(taskMapper::toTaskDto).toList()
        );
    }

    private Double calculateChecklistProgress(List<Item> tasks){
        if(tasks == null || tasks.isEmpty()) return null;
        long numberOfTaskDone = tasks.stream().filter(task -> task.getItemStatus() == ItemStatus.DONE).count();
        return (double) numberOfTaskDone / tasks.size();
    }
}
