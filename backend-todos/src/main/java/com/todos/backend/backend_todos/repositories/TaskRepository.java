package com.todos.backend.backend_todos.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;

public interface TaskRepository {
    public Task save(Task task);
    public Optional<Task> findById(UUID id);
    public void delete(Task task);
    public Page<Task> findByDoneTextAndPriority(Boolean done, String text, Priority priority, Pageable pageable);
}
