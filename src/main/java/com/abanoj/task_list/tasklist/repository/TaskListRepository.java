package com.abanoj.task_list.tasklist.repository;

import com.abanoj.task_list.tasklist.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
}
