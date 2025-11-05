package com.abanoj.task_list.task.entities;

import com.abanoj.task_list.tasklist.entities.TaskList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
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
}
