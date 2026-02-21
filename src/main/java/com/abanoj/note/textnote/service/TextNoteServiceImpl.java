package com.abanoj.note.textnote.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.textnote.repository.TextNoteRepository;
import com.abanoj.note.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextNoteServiceImpl implements TextNoteService {

    private final TextNoteRepository textNoteRepository;
    private final SecurityUtils securityUtils;

    @Override
    public List<TextNote> findAllTextNote() {
        User user = securityUtils.getCurrentUser();
        return textNoteRepository.findAllByUser(user);
    }

    @Override
    public TextNote findTextNoteById(Long textNoteId) {
        User user = securityUtils.getCurrentUser();
        return textNoteRepository
                .findByIdAndUser(textNoteId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Text note with id " + textNoteId + " not found!"));
    }

    @Override
    @Transactional
    public TextNote createTextNote(TextNote textNote) {
        User user = securityUtils.getCurrentUser();
        if(textNote.getId() != null) throw new IllegalArgumentException("Text note already has and ID!");
        LocalDateTime now = LocalDateTime.now();
        textNote.setUser(user);
        textNote.setCreated(now);
        textNote.setUpdated(now);
        TextNote savedTextNote = textNoteRepository.save(textNote);
        log.debug("TextNote created with id {}", savedTextNote.getId());
        return savedTextNote;
    }

    @Override
    @Transactional
    public TextNote updateTextNote(Long textNoteId, TextNote textNote) {
        User user = securityUtils.getCurrentUser();
        if(textNote.getId() == null) throw new IllegalArgumentException("TextNote must have an ID");
        if(!Objects.equals(textNote.getId(), textNoteId)) throw new IllegalArgumentException("Id and TextNote id do not match");

        TextNote textNoteToUpdate = textNoteRepository
                .findByIdAndUser(textNoteId, user)
                .orElseThrow(() -> new ResourceNotFoundException("TextNote with id " + textNoteId + " not found!"));

        textNoteToUpdate.setTitle(textNote.getTitle());
        textNoteToUpdate.setContent(textNote.getContent());
        textNoteToUpdate.setUpdated(LocalDateTime.now());

        log.debug("TextNote {} updated", textNoteId);
        return textNoteRepository.save(textNoteToUpdate);

    }

    @Override
    @Transactional
    public void deleteTextNote(Long textNoteId) {
        User user = securityUtils.getCurrentUser();
        TextNote textNote = textNoteRepository
                .findByIdAndUser(textNoteId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Text note with id " + textNoteId + " not found!"));
        textNoteRepository.delete(textNote);
        log.debug("Text note {} delete", textNoteId);

    }
}
