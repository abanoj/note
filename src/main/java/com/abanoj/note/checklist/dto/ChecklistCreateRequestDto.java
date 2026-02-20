package com.abanoj.note.checklist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChecklistCreateRequestDto(
        @Schema(description = "Checklist title", example = "Shopping list")
        @NotBlank(message = "There must be a title") String title) {
}
