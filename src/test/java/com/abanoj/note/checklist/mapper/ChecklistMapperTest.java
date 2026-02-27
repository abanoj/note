package com.abanoj.note.checklist.mapper;

import com.abanoj.note.checklist.dto.ChecklistCreateRequestDto;
import com.abanoj.note.checklist.dto.ChecklistResponseDto;
import com.abanoj.note.checklist.dto.ChecklistUpdateRequestDto;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.item.mapper.ItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChecklistMapperTest {

    @Mock
    private ItemMapper itemMapper;

    private ChecklistMapper checklistMapper;

    @BeforeEach
    void setUp() {
        checklistMapper = new ChecklistMapper(itemMapper);
    }

    @Test
    void toChecklistFromCreateDtoShouldMapTitleAndInitEmptyItems() {
        ChecklistCreateRequestDto dto = new ChecklistCreateRequestDto("Shopping list");

        Checklist checklist = checklistMapper.toChecklist(dto);

        assertThat(checklist.getTitle()).isEqualTo("Shopping list");
        assertThat(checklist.getItems()).isNotNull().isEmpty();
        assertThat(checklist.getId()).isNull();
    }

    @Test
    void toChecklistFromUpdateDtoShouldMapIdAndTitle() {
        ChecklistUpdateRequestDto dto = new ChecklistUpdateRequestDto(1L, "Updated title");

        Checklist checklist = checklistMapper.toChecklist(dto);

        assertThat(checklist.getId()).isEqualTo(1L);
        assertThat(checklist.getTitle()).isEqualTo("Updated title");
    }

    @Test
    void toChecklistResponseDtoWithEmptyItemsShouldReturnNullProgress() {
        Checklist checklist = Checklist.builder()
                .id(1L)
                .title("Empty list")
                .items(new ArrayList<>())
                .build();

        ChecklistResponseDto dto = checklistMapper.toChecklistResponseDto(checklist);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("Empty list");
        assertThat(dto.numberOfItems()).isZero();
        assertThat(dto.progress()).isNull();
        assertThat(dto.items()).isEmpty();
    }

    @Test
    void toChecklistResponseDtoAllItemsDoneShouldReturnProgress1() {
        Item item1 = new Item(1L, "Item 1", ItemStatus.DONE, ItemPriority.HIGH, null, null, null);
        Item item2 = new Item(2L, "Item 2", ItemStatus.DONE, ItemPriority.LOW, null, null, null);
        Checklist checklist = Checklist.builder()
                .id(1L)
                .title("All done")
                .items(List.of(item1, item2))
                .build();

        when(itemMapper.toItemDto(any())).thenReturn(null);

        ChecklistResponseDto dto = checklistMapper.toChecklistResponseDto(checklist);

        assertThat(dto.numberOfItems()).isEqualTo(2);
        assertThat(dto.progress()).isEqualTo(1.0);
    }

    @Test
    void toChecklistResponseDtoMixedItemsShouldCalculateProgress() {
        Item done = new Item(1L, "Done", ItemStatus.DONE, ItemPriority.HIGH, null, null, null);
        Item pending = new Item(2L, "Pending", ItemStatus.PENDING, ItemPriority.LOW, null, null, null);
        Item inProgress = new Item(3L, "In Progress", ItemStatus.IN_PROGRESS, ItemPriority.MEDIUM, null, null, null);
        Checklist checklist = Checklist.builder()
                .id(1L)
                .title("Mixed")
                .items(List.of(done, pending, inProgress))
                .build();

        when(itemMapper.toItemDto(any())).thenReturn(null);

        ChecklistResponseDto dto = checklistMapper.toChecklistResponseDto(checklist);

        assertThat(dto.numberOfItems()).isEqualTo(3);
        assertThat(dto.progress()).isCloseTo(1.0 / 3.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void toChecklistResponseDtoNoDoneItemsShouldReturnProgress0() {
        Item pending = new Item(1L, "Pending", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);
        Checklist checklist = Checklist.builder()
                .id(1L)
                .title("No progress")
                .items(List.of(pending))
                .build();

        when(itemMapper.toItemDto(any())).thenReturn(null);

        ChecklistResponseDto dto = checklistMapper.toChecklistResponseDto(checklist);

        assertThat(dto.numberOfItems()).isEqualTo(1);
        assertThat(dto.progress()).isEqualTo(0.0);
    }
}
