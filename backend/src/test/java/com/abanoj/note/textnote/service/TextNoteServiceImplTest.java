package com.abanoj.note.textnote.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.textnote.repository.TextNoteRepository;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextNoteServiceImplTest {

    @Mock
    private TextNoteRepository textNoteRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TextNoteServiceImpl textNoteService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
    }

    @Test
    void findAllTextNoteShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        TextNote note = TextNote.builder().id(1L).title("Note").build();
        Page<TextNote> page = new PageImpl<>(List.of(note));

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findAllByUser(user, pageable)).thenReturn(page);

        Page<TextNote> result = textNoteService.findAllTextNote(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findTextNoteByIdShouldReturnNoteWhenFound() {
        TextNote note = TextNote.builder().id(1L).title("Note").content("Content").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(note));

        TextNote result = textNoteService.findTextNoteById(1L);

        assertThat(result.getTitle()).isEqualTo("Note");
    }

    @Test
    void findTextNoteByIdShouldThrowWhenNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> textNoteService.findTextNoteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTextNoteShouldSaveAndReturn() {
        TextNote input = TextNote.builder().title("New Note").content("Content").build();
        TextNote saved = TextNote.builder().id(1L).title("New Note").content("Content").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.save(any(TextNote.class))).thenReturn(saved);

        TextNote result = textNoteService.createTextNote(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(textNoteRepository).save(any(TextNote.class));
    }

    @Test
    void createTextNoteShouldThrowWhenIdAlreadyExists() {
        TextNote input = TextNote.builder().id(1L).title("Existing").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> textNoteService.createTextNote(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateTextNoteShouldUpdateAndReturn() {
        TextNote input = TextNote.builder().id(1L).title("Updated").content("New content").build();
        TextNote existing = TextNote.builder().id(1L).title("Old").content("Old content").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
        when(textNoteRepository.save(any(TextNote.class))).thenReturn(existing);

        TextNote result = textNoteService.updateTextNote(1L, input);

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.getContent()).isEqualTo("New content");
    }

    @Test
    void updateTextNoteShouldThrowWhenNullId() {
        TextNote input = TextNote.builder().title("No ID").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> textNoteService.updateTextNote(1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have an ID");
    }

    @Test
    void updateTextNoteShouldThrowWhenIdMismatch() {
        TextNote input = TextNote.builder().id(2L).title("Mismatch").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> textNoteService.updateTextNote(1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    void updateTextNoteShouldThrowWhenNotFound() {
        TextNote input = TextNote.builder().id(1L).title("Not found").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> textNoteService.updateTextNote(1L, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteTextNoteShouldDeleteWhenFound() {
        TextNote note = TextNote.builder().id(1L).title("To delete").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(note));

        textNoteService.deleteTextNote(1L);

        verify(textNoteRepository).delete(note);
    }

    @Test
    void deleteTextNoteShouldThrowWhenNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(textNoteRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> textNoteService.deleteTextNote(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
