package com.abanoj.note.textnote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TextNoteUpdateRequestDto(
        @Schema(description = "TextNote ID", example = "1")
        @NotNull(message = "There must be an ID")
        Long id,
        @Schema(description = "TextNote title", example = "Memories")
        @NotBlank(message = "There must be a title")
        String title,
        @Schema(description = "TextNote content text", example = "Some text...")
        String content
) {
}
