package com.abanoj.note.textnote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TextNoteResponseDto(
        @Schema(description = "TextNote ID", example = "1")
        Long id,
        @Schema(description = "TextNote title", example = "Memories")
        String title,
        @Schema(description = "TextNote content text", example = "Some text...")
        String content
) {
}
