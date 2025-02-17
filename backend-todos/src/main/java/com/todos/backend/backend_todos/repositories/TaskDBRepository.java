package com.todos.backend.backend_todos.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;

public interface TaskDBRepository extends JpaRepository<Task, UUID>, TaskRepository {
    String FILTER_QUERY_BY_DONE_TEXT_AND_PRIORITY = "SELECT t FROM Task t WHERE " + 
        "(:done IS NULL OR t.done = :done) " + 
        "AND (:text IS NULL OR LOWER(t.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
        "AND (:priority IS NULL OR t.priority = :priority)";

    @Override
    @Query(FILTER_QUERY_BY_DONE_TEXT_AND_PRIORITY)
    Page<Task> findByDoneTextAndPriority(@Param("done") Boolean done, @Param("text") String text, @Param("priority") Priority priority, Pageable pageable);
}
