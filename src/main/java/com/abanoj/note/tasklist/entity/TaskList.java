package com.abanoj.note.tasklist.entity;

import com.abanoj.note.task.entity.Task;
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
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
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
        TaskList taskList = (TaskList) o;
        return Objects.equals(id, taskList.id) && Objects.equals(title, taskList.title) && Objects.equals(created, taskList.created) && Objects.equals(updated, taskList.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, created, updated);
    }
}
