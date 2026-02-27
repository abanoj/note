package com.abanoj.note.textnote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TextNoteResponseDto(
        @Schema(description = "TextNote ID", example = "1")
        Long id,
        @Schema(description = "TextNote title", example = "Memories")
        String title,
        @Schema(description = "TextNote content text", example = "Some text...")
        String content,
        @Schema(description = "Created date", example = "")
        LocalDateTime created,
        @Schema(description = "Updated date", example = "")
        LocalDateTime updated
) {
}
