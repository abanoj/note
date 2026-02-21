package com.abanoj.note.textnote.service;

import com.abanoj.note.textnote.entity.TextNote;

import java.util.List;

public interface TextNoteService {
    List<TextNote> findAllTextNote();
    TextNote findTextNoteById(Long textNoteId);
    TextNote createTextNote(TextNote textNote);
    TextNote updateTextNote(Long textNoteId, TextNote textNote);
    void deleteTextNote(Long textNoteId);
}
