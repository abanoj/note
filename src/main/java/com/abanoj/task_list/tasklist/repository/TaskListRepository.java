package com.abanoj.task_list.tasklist.repository;

import com.abanoj.task_list.tasklist.entity.TaskList;
import com.abanoj.task_list.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findAllByUser(User user);
    Optional<TaskList> findByIdAndUser(Long id, User user);
}
