package com.abanoj.note.checklist.repository;

import com.abanoj.note.checklist.entity.Checklist;
import com.abanoj.note.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    @Query("SELECT DISTINCT tl FROM Checklist tl LEFT JOIN FETCH tl.tasks WHERE tl.user = :user")
    List<Checklist> findAllByUser(@Param("user") User user);
    @Query("SELECT tl FROM Checklist tl LEFT JOIN FETCH tl.tasks WHERE tl.id = :id AND tl.user = :user")
    Optional<Checklist> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    boolean existsByIdAndUser(Long id, User user);
}
