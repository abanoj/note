package com.abanoj.note.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChecklistUpdateRequestDto(
        @Schema(description = "Checklist ID", example = "1")
        @NotNull(message = "There must be an ID")
        Long id,
        @Schema(description = "Checklist title", example = "Shopping list")
        @NotBlank(message = "There must be a title")
        String title) {
}
