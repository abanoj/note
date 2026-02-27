package com.abanoj.note.checklist.repository;

import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChecklistRepositoryTest {

    @Autowired
    private ChecklistRepository checklistRepository;

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

    private Checklist createChecklist(String title, User owner) {
        LocalDateTime now = LocalDateTime.now();
        Checklist checklist = Checklist.builder()
                .title(title)
                .items(new ArrayList<>())
                .created(now)
                .updated(now)
                .user(owner)
                .build();
        return entityManager.persist(checklist);
    }

    @Test
    void findAllByUserShouldReturnOnlyOwnChecklists() {
        createChecklist("My list", user);
        createChecklist("Other's list", otherUser);
        entityManager.flush();

        Page<Checklist> result = checklistRepository.findAllByUser(user, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("My list");
    }

    @Test
    void findAllByUserShouldSupportPagination() {
        for (int i = 0; i < 5; i++) {
            createChecklist("List " + i, user);
        }
        entityManager.flush();

        Page<Checklist> firstPage = checklistRepository.findAllByUser(user, PageRequest.of(0, 2, Sort.by("title")));
        Page<Checklist> secondPage = checklistRepository.findAllByUser(user, PageRequest.of(1, 2, Sort.by("title")));

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
    }

    @Test
    void findAllByUserShouldReturnEmptyWhenNoChecklists() {
        Page<Checklist> result = checklistRepository.findAllByUser(user, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdAndUserShouldReturnChecklistWhenOwned() {
        Checklist checklist = createChecklist("My list", user);
        entityManager.flush();

        Optional<Checklist> result = checklistRepository.findByIdAndUser(checklist.getId(), user);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("My list");
    }

    @Test
    void findByIdAndUserShouldReturnEmptyWhenNotOwned() {
        Checklist checklist = createChecklist("Other's list", otherUser);
        entityManager.flush();

        Optional<Checklist> result = checklistRepository.findByIdAndUser(checklist.getId(), user);

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdAndUserShouldReturnEmptyWhenNotExists() {
        Optional<Checklist> result = checklistRepository.findByIdAndUser(999L, user);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByIdAndUserShouldReturnTrueWhenOwned() {
        Checklist checklist = createChecklist("My list", user);
        entityManager.flush();

        boolean exists = checklistRepository.existsByIdAndUser(checklist.getId(), user);

        assertThat(exists).isTrue();
    }
}
