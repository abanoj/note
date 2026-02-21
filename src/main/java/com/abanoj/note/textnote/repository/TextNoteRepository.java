package com.abanoj.note.textnote.repository;

import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TextNoteRepository extends JpaRepository<TextNote, Long> {
    List<TextNote> findAllByUser(User user);
    Optional<TextNote> findByIdAndUser(Long textNoteId, User user);
}
