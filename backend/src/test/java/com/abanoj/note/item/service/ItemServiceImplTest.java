package com.abanoj.note.item.service;

import com.abanoj.note.auth.SecurityUtils;
import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.checklist.repository.ChecklistRepository;
import com.abanoj.note.exception.ResourceNotFoundException;
import com.abanoj.note.item.entity.Item;
import com.abanoj.note.item.entity.ItemPriority;
import com.abanoj.note.item.entity.ItemStatus;
import com.abanoj.note.item.repository.ItemRepository;
import com.abanoj.note.user.Role;
import com.abanoj.note.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Checklist checklist;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        checklist = Checklist.builder()
                .id(1L)
                .title("My List")
                .items(new ArrayList<>())
                .user(user)
                .build();
    }

    private void mockOwnerCheck() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(checklist));
    }

    @Test
    void findItemShouldReturnItemWhenFound() {
        Item item = new Item(1L, "Buy milk", ItemStatus.PENDING, ItemPriority.HIGH, checklist, LocalDateTime.now(), LocalDateTime.now());

        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 1L)).thenReturn(Optional.of(item));

        Item result = itemService.findItem(1L, 1L);

        assertThat(result.getTitle()).isEqualTo("Buy milk");
    }

    @Test
    void findItemShouldThrowWhenChecklistNotOwned() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findItem(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findItemShouldThrowWhenItemNotFound() {
        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findItem(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAllItemsShouldReturnList() {
        Item item = new Item(1L, "Item", ItemStatus.PENDING, ItemPriority.MEDIUM, checklist, LocalDateTime.now(), LocalDateTime.now());

        mockOwnerCheck();
        when(itemRepository.findByChecklistId(1L)).thenReturn(List.of(item));

        List<Item> result = itemService.findAllItems(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllItemsShouldThrowWhenNotOwned() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findAllItems(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createItemShouldSaveAndReturn() {
        Item input = new Item(null, "New Item", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);
        Item saved = new Item(1L, "New Item", ItemStatus.PENDING, ItemPriority.HIGH, checklist, LocalDateTime.now(), LocalDateTime.now());

        mockOwnerCheck();
        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        Item result = itemService.createItem(1L, input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemShouldThrowWhenChecklistNotOwned() {
        Item input = new Item(null, "Item", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(checklistRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(1L, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateItemShouldUpdateAndReturn() {
        Item input = new Item(1L, "Updated", ItemStatus.DONE, ItemPriority.LOW, null, null, null);
        Item existing = new Item(1L, "Old", ItemStatus.PENDING, ItemPriority.HIGH, checklist, LocalDateTime.now(), LocalDateTime.now());

        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenReturn(existing);

        Item result = itemService.updateItem(1L, 1L, input);

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.getItemStatus()).isEqualTo(ItemStatus.DONE);
    }

    @Test
    void updateItemShouldThrowWhenNullId() {
        Item input = new Item(null, "No ID", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        assertThatThrownBy(() -> itemService.updateItem(1L, 1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must have an ID");
    }

    @Test
    void updateItemShouldThrowWhenIdMismatch() {
        Item input = new Item(2L, "Mismatch", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        assertThatThrownBy(() -> itemService.updateItem(1L, 1L, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    void updateItemShouldThrowWhenNotFound() {
        Item input = new Item(1L, "Not found", ItemStatus.PENDING, ItemPriority.HIGH, null, null, null);

        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, 1L, input))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteItemShouldRemoveFromChecklist() {
        Item item = new Item(1L, "To delete", ItemStatus.PENDING, ItemPriority.HIGH, checklist, LocalDateTime.now(), LocalDateTime.now());
        checklist.getItems().add(item);

        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 1L);

        assertThat(checklist.getItems()).doesNotContain(item);
    }

    @Test
    void deleteItemShouldThrowWhenNotFound() {
        mockOwnerCheck();
        when(itemRepository.findByChecklistIdAndId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteItem(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
