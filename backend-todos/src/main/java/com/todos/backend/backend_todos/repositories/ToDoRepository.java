package com.todos.backend.backend_todos.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todos.backend.backend_todos.models.ToDo;

public interface ToDoRepository  extends JpaRepository<ToDo, UUID> {

}
