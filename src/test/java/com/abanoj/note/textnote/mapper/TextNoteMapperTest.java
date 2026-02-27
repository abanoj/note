package com.abanoj.note.textnote.mapper;

import com.abanoj.note.textnote.dto.TextNoteCreateRequestDto;
import com.abanoj.note.textnote.dto.TextNoteResponseDto;
import com.abanoj.note.textnote.dto.TextNoteUpdateRequestDto;
import com.abanoj.note.textnote.entity.TextNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TextNoteMapperTest {

    private TextNoteMapper textNoteMapper;

    @BeforeEach
    void setUp() {
        textNoteMapper = new TextNoteMapper();
    }

    @Test
    void toTextNoteFromCreateDtoShouldMapTitleAndContent() {
        TextNoteCreateRequestDto dto = new TextNoteCreateRequestDto("Memories", "Some text...");

        TextNote textNote = textNoteMapper.toTextNote(dto);

        assertThat(textNote.getTitle()).isEqualTo("Memories");
        assertThat(textNote.getContent()).isEqualTo("Some text...");
        assertThat(textNote.getId()).isNull();
    }

    @Test
    void toTextNoteFromUpdateDtoShouldMapIdTitleAndContent() {
        TextNoteUpdateRequestDto dto = new TextNoteUpdateRequestDto(1L, "Updated", "Updated content");

        TextNote textNote = textNoteMapper.toTextNote(dto);

        assertThat(textNote.getId()).isEqualTo(1L);
        assertThat(textNote.getTitle()).isEqualTo("Updated");
        assertThat(textNote.getContent()).isEqualTo("Updated content");
    }

    @Test
    void toTextNoteResponseDtoShouldMapAllFields() {
        LocalDateTime now = LocalDateTime.now();
        TextNote textNote = TextNote.builder()
                .id(1L)
                .title("My Note")
                .content("Content here")
                .created(now)
                .updated(now)
                .build();

        TextNoteResponseDto dto = textNoteMapper.toTextNoteResponseDto(textNote);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("My Note");
        assertThat(dto.content()).isEqualTo("Content here");
        assertThat(dto.created()).isEqualTo(now);
        assertThat(dto.updated()).isEqualTo(now);
    }

    @Test
    void toTextNoteFromCreateDtoShouldHandleNullContent() {
        TextNoteCreateRequestDto dto = new TextNoteCreateRequestDto("Title only", null);

        TextNote textNote = textNoteMapper.toTextNote(dto);

        assertThat(textNote.getTitle()).isEqualTo("Title only");
        assertThat(textNote.getContent()).isNull();
    }
}
