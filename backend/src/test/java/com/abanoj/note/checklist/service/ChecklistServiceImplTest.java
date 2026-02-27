package com.abanoj.note.checklist.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.checklist.repository.ChecklistRepository;
import com.abanoj.note.exception.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChecklistServiceImplTest {

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ChecklistServiceImpl checklistService;

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
    void findAllChecklistShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Checklist checklist = Checklist.builder().id(1L).title("List").items(new ArrayList<>()).build();
        Page<Checklist> page = new PageImpl<>(List.of(checklist));

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findAllByUser(user, pageable)).thenReturn(page);

        Page<Checklist> result = checklistService.findAllChecklist(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(checklistRepository).findAllByUser(user, pageable);
    }

    @Test
    void findChecklistShouldReturnChecklistWhenFound() {
        Checklist checklist = Checklist.builder().id(1L).title("List").items(new ArrayList<>()).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(checklist));

        Checklist result = checklistService.findChecklist(1L);

        assertThat(result.getTitle()).isEqualTo("List");
    }

    @Test
    void findChecklistShouldThrowWhenNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checklistService.findChecklist(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createChecklistShouldSaveAndReturn() {
        Checklist checklist = Checklist.builder().title("New List").items(new ArrayList<>()).build();
        Checklist saved = Checklist.builder().id(1L).title("New List").items(new ArrayList<>()).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.save(any(Checklist.class))).thenReturn(saved);

        Checklist result = checklistService.createChecklist(checklist);

        assertThat(result.getId()).isEqualTo(1L);
        verify(checklistRepository).save(any(Checklist.class));
    }

    @Test
    void createChecklistShouldThrowWhenIdAlreadyExists() {
        Checklist checklist = Checklist.builder().id(1L).title("Existing").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> checklistService.createChecklist(checklist))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateChecklistShouldUpdateAndReturn() {
        Checklist input = Checklist.builder().id(1L).title("Updated").build();
        Checklist existing = Checklist.builder().id(1L).title("Old").items(new ArrayList<>()).build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
        when(checklistRepository.save(any(Checklist.class))).thenReturn(existing);

        Checklist result = checklistService.updateChecklist(1L, input);

        assertThat(result.getTitle()).isEqualTo("Updated");
        verify(checklistRepository).save(existing);
    }

    @Test
    void updateChecklistShouldThrowWhenNullId() {
        Checklist input = Checklist.builder().title("No ID").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> checklistService.updateChecklist(1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have an ID");
    }

    @Test
    void updateChecklistShouldThrowWhenIdMismatch() {
        Checklist input = Checklist.builder().id(2L).title("Mismatch").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThatThrownBy(() -> checklistService.updateChecklist(1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    void updateChecklistShouldThrowWhenNotFound() {
        Checklist input = Checklist.builder().id(1L).title("Not found").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checklistService.updateChecklist(1L, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteChecklistShouldDeleteWhenFound() {
        Checklist checklist = Checklist.builder().id(1L).title("To delete").build();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(checklist));

        checklistService.deleteChecklist(1L);

        verify(checklistRepository).delete(checklist);
    }

    @Test
    void deleteChecklistShouldThrowWhenNotFound() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checklistService.deleteChecklist(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
