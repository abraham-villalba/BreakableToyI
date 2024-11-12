package com.todos.backend.backend_todos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoRepository;

@Service
public class ToDoService {
    @Autowired
    private ToDoRepository repository;

    public ToDoService() {
    }

    public ToDo saveToDo(ToDo toDo) {
        return repository.save(toDo);
    }
    
}
