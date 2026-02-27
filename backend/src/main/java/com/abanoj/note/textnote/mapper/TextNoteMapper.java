package com.abanoj.note.textnote.mapper;

import com.abanoj.note.textnote.dto.TextNoteCreateRequestDto;
import com.abanoj.note.textnote.dto.TextNoteResponseDto;
import com.abanoj.note.textnote.dto.TextNoteUpdateRequestDto;
import com.abanoj.note.textnote.entity.TextNote;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TextNoteMapper {

    public TextNote toTextNote(TextNoteCreateRequestDto textNoteCreateRequestDto){
        return TextNote.builder()
                .title(textNoteCreateRequestDto.title())
                .content(textNoteCreateRequestDto.content())
                .build();
    }

    public TextNote toTextNote(TextNoteUpdateRequestDto textNoteUpdateRequestDto){
        return TextNote.builder()
                .id(textNoteUpdateRequestDto.id())
                .title(textNoteUpdateRequestDto.title())
                .content(textNoteUpdateRequestDto.content())
                .build();
    }

    public TextNoteResponseDto toTextNoteResponseDto(TextNote textNote){
        return new TextNoteResponseDto(
                textNote.getId(),
                textNote.getTitle(),
                textNote.getContent(),
                textNote.getCreated(),
                textNote.getUpdated()
        );
    }
}
