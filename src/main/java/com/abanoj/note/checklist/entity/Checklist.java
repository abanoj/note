package com.abanoj.note.checklist.entity;

import com.abanoj.note.item.entity.Item;
import com.abanoj.note.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> tasks = new ArrayList<>();
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime updated;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Checklist checklist = (Checklist) o;
        return Objects.equals(id, checklist.id) && Objects.equals(title, checklist.title) && Objects.equals(created, checklist.created) && Objects.equals(updated, checklist.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, created, updated);
    }
}
