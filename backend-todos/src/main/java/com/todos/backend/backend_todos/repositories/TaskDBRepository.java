package com.todos.backend.backend_todos.repositories;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;

/**
 * Repository for Task entity using JPA and Hibernate
 * 
 */
@Repository
@Profile("db")
public interface TaskDBRepository extends JpaRepository<Task, UUID>, TaskRepository {
    String FILTER_QUERY_BY_DONE_TEXT_AND_PRIORITY = "SELECT t FROM Task t WHERE " + 
        "(:done IS NULL OR t.done = :done) " + 
        "AND (:text IS NULL OR LOWER(t.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
        "AND (:priority IS NULL OR t.priority = :priority)";

    /**
     * Find tasks by done, text and priority
     * 
     * @param done Task done status to search
     * @param text Task task description to search
     * @param priority Task priority to search
     * @param pageable Pageable object to paginate the results
     * @return Page of tasks that match the search criteria
     */
    @Override
    @Query(FILTER_QUERY_BY_DONE_TEXT_AND_PRIORITY)
    Page<Task> findByDoneTextAndPriority(@Param("done") Boolean done, @Param("text") String text, @Param("priority") Priority priority, Pageable pageable);
}
