package com.abanoj.note.textnote.service;

import com.abanoj.note.textnote.entity.TextNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TextNoteService {
    Page<TextNote> findAllTextNote(Pageable pageable);
    TextNote findTextNoteById(Long textNoteId);
    TextNote createTextNote(TextNote textNote);
    TextNote updateTextNote(Long textNoteId, TextNote textNote);
    void deleteTextNote(Long textNoteId);
}
