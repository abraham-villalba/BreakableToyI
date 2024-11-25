package com.todos.backend.backend_todos.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;

public interface ToDoRepository {
    public ToDo save(ToDo todo);
    public Optional<ToDo> findById(UUID id);
    public void delete(ToDo toDo);
    public Page<ToDo> findByDoneTextAndPriority(Boolean done, String text, Priority priority, Pageable pageable);
}
