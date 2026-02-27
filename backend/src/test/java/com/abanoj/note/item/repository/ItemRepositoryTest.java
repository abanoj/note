package com.abanoj.note.item.repository;

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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Checklist checklist;
    private Checklist otherChecklist;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        entityManager.persist(user);

        LocalDateTime now = LocalDateTime.now();
        checklist = Checklist.builder()
                .title("My List")
                .items(new ArrayList<>())
                .created(now)
                .updated(now)
                .user(user)
                .build();
        entityManager.persist(checklist);

        otherChecklist = Checklist.builder()
                .title("Other List")
                .items(new ArrayList<>())
                .created(now)
                .updated(now)
                .user(user)
                .build();
        entityManager.persist(otherChecklist);

        entityManager.flush();
    }

    private Item createItem(String title, Checklist parentChecklist) {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(null, title, ItemStatus.PENDING, ItemPriority.MEDIUM, parentChecklist, now, now);
        return entityManager.persist(item);
    }

    @Test
    void findByChecklistIdShouldReturnItems() {
        createItem("Item 1", checklist);
        createItem("Item 2", checklist);
        createItem("Other item", otherChecklist);
        entityManager.flush();

        List<Item> result = itemRepository.findByChecklistId(checklist.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findByChecklistIdShouldReturnEmptyWhenNoItems() {
        List<Item> result = itemRepository.findByChecklistId(checklist.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByChecklistIdAndIdShouldReturnItem() {
        Item item = createItem("Item 1", checklist);
        entityManager.flush();

        Optional<Item> result = itemRepository.findByChecklistIdAndId(checklist.getId(), item.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Item 1");
    }

    @Test
    void findByChecklistIdAndIdShouldReturnEmptyWhenWrongChecklist() {
        Item item = createItem("Item 1", checklist);
        entityManager.flush();

        Optional<Item> result = itemRepository.findByChecklistIdAndId(otherChecklist.getId(), item.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByChecklistIdAndIdShouldReturnEmptyWhenNotExists() {
        Optional<Item> result = itemRepository.findByChecklistIdAndId(checklist.getId(), 999L);

        assertThat(result).isEmpty();
    }
}
