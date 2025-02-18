package com.todos.backend.backend_todos.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;

/**
 * Repository for Task entity
 */
public interface TaskRepository {
    /**
     * Save a task
     * @param task Task to save
     * @return Saved task
     */
    public Task save(Task task);
    /**
     * Find a task by its ID
     * @param id ID of the task to find
     * @return Task with the given ID or null if not found
     */
    public Optional<Task> findById(UUID id);
    /**
     * Delete a task
     * @param task Task to delete
     */
    public void delete(Task task);
    /**
     * Find all tasks
     * @param pageable Pageable object to paginate the results
     * @return Page of tasks
     */
    public Page<Task> findByDoneTextAndPriority(Boolean done, String text, Priority priority, Pageable pageable);
}
