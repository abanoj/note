package com.abanoj.note.textnote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TextNoteCreateRequestDto(
        @Schema(description = "TextNote title", example = "Memories")
        @NotBlank(message = "There must be a title")
        String title,
        @Schema(description = "TextNote content", example = "Some text...")
        String content
) {
}
