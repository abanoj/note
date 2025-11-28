package com.abanoj.task_list.task.entities;

import com.abanoj.task_list.tasklist.entities.TaskList;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private TaskStatus taskStatus;
    @Column(nullable = false)
    private TaskPriority taskPriority;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskList taskList;
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime updated;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && taskStatus == task.taskStatus && taskPriority == task.taskPriority && Objects.equals(created, task.created) && Objects.equals(updated, task.updated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, taskStatus, taskPriority, created, updated);
    }
}
