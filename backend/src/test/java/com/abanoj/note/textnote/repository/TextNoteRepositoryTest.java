package com.abanoj.note.textnote.repository;

import com.abanoj.note.textnote.entity.TextNote;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TextNoteRepositoryTest {

    @Autowired
    private TextNoteRepository textNoteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        entityManager.persist(user);

        otherUser = User.builder()
                .email("other@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        entityManager.persist(otherUser);

        entityManager.flush();
    }

    private TextNote createTextNote(String title, User owner) {
        LocalDateTime now = LocalDateTime.now();
        TextNote note = TextNote.builder()
                .title(title)
                .content("Content for " + title)
                .created(now)
                .updated(now)
                .user(owner)
                .build();
        return entityManager.persist(note);
    }

    @Test
    void findAllByUserShouldReturnOnlyOwnNotes() {
        createTextNote("My note", user);
        createTextNote("Other note", otherUser);
        entityManager.flush();

        Page<TextNote> result = textNoteRepository.findAllByUser(user, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("My note");
    }

    @Test
    void findAllByUserShouldSupportPagination() {
        for (int i = 0; i < 5; i++) {
            createTextNote("Note " + i, user);
        }
        entityManager.flush();

        Page<TextNote> page = textNoteRepository.findAllByUser(user, PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
    }

    @Test
    void findAllByUserShouldReturnEmptyWhenNoNotes() {
        Page<TextNote> result = textNoteRepository.findAllByUser(user, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdAndUserShouldReturnNoteWhenOwned() {
        TextNote note = createTextNote("My note", user);
        entityManager.flush();

        Optional<TextNote> result = textNoteRepository.findByIdAndUser(note.getId(), user);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("My note");
    }

    @Test
    void findByIdAndUserShouldReturnEmptyWhenNotOwned() {
        TextNote note = createTextNote("Other's note", otherUser);
        entityManager.flush();

        Optional<TextNote> result = textNoteRepository.findByIdAndUser(note.getId(), user);

        assertThat(result).isEmpty();
    }
}
