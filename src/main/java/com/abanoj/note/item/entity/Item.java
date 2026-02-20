package com.abanoj.note.item.entity;

import com.abanoj.note.checklist.entity.Checklist;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemPriority itemPriority;
    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime updated;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(title, item.title) && itemStatus == item.itemStatus && itemPriority == item.itemPriority && Objects.equals(checklist, item.checklist) && Objects.equals(created, item.created) && Objects.equals(updated, item.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, itemStatus, itemPriority, checklist, created, updated);
    }
}
