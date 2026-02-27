package com.abanoj.note.checklist.dto;

import com.abanoj.note.item.dto.ItemDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ChecklistResponseDto(
        @Schema(description = "Checklist ID", example = "1")
        Long id,
        @Schema(description = "Checklist title", example = "Shopping list")
        String title,
        @Schema(description = "Total number of items", example = "5")
        Integer numberOfItems,
        @Schema(description = "Completion progress (0.0 to 1.0)", example = "0.6")
        Double progress,
        @Schema(description = "List of items")
        List<ItemDto> items
) {
}
