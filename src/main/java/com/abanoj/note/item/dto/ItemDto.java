package com.abanoj.note.item.dto;

import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ItemDto(
        @Schema(description = "Item ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,
        @Schema(description = "Item title", example = "Buy groceries")
        @NotBlank(message = "There must be a title") String title,
        @Schema(description = "Item status", example = "PENDING")
        ItemStatus status,
        @Schema(description = "Item priority", example = "HIGH")
        ItemPriority priority) {
}
