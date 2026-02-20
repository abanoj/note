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
    @Query("SELECT DISTINCT cl FROM Checklist cl LEFT JOIN FETCH cl.items WHERE cl.user = :user")
    List<Checklist> findAllByUser(@Param("user") User user);
    @Query("SELECT cl FROM Checklist cl LEFT JOIN FETCH cl.items WHERE cl.id = :id AND cl.user = :user")
    Optional<Checklist> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
    boolean existsByIdAndUser(Long id, User user);
}
