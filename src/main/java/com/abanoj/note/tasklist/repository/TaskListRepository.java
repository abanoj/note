package com.abanoj.note.tasklist.repository;

import com.abanoj.note.tasklist.entity.TaskList;
import com.abanoj.note.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    @Query("SELECT DISTINCT tl FROM TaskList tl LEFT JOIN FETCH tl.tasks WHERE tl.user = :user")
    List<TaskList> findAllByUser(@Param("user") User user);
    @Query("SELECT tl FROM TaskList tl LEFT JOIN FETCH tl.tasks WHERE tl.id = :id AND tl.user = :user")
    Optional<TaskList> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    boolean existsByIdAndUser(Long id, User user);
}
